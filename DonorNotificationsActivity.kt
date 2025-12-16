package com.example.vublooddonationsociety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.models.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// Data class for the donor's own info
data class Donor(
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val area: String = "",
    val approved: Boolean = false
)

// Data class for admin notifications
data class NotificationMessage(
    val title: String = "",
    val message: String = "",
    val timestamp: String = ""
)

class DonorNotificationsActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = auth.currentUser?.uid ?: ""

        setContent {
            var donor by remember { mutableStateOf<Donor?>(null) }
            var matchingRequests by remember { mutableStateOf(listOf<BloodRequest>()) }
            var adminNotifications by remember { mutableStateOf(listOf<NotificationMessage>()) }

            // Load donor info
            LaunchedEffect(uid) {
                db.getReference("donors").child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            donor = snapshot.getValue(Donor::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                // Load admin notifications
                db.getReference("notifications").child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = mutableListOf<NotificationMessage>()
                            for (child in snapshot.children) {
                                val msg = child.getValue(NotificationMessage::class.java)
                                if (msg != null) list.add(msg)
                            }
                            adminNotifications = list.reversed()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            // Load matching blood requests once donor data is available
            LaunchedEffect(donor) {
                val donorData = donor
                if (donorData != null) {
                    db.getReference("requests").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = mutableListOf<BloodRequest>()

                            for (userNode in snapshot.children) {
                                for (child in userNode.children) {
                                    val req = child.getValue(BloodRequest::class.java)
                                    val approved = child.child("approved").getValue(Boolean::class.java) ?: false
                                    val bloodGroup = child.child("bloodGroup").getValue(String::class.java)?.trim() ?: ""
                                    val area = child.child("area").getValue(String::class.java)?.trim() ?: ""

                                    if (
                                        approved &&
                                        bloodGroup.equals(donorData.bloodGroup.trim(), ignoreCase = true) &&
                                        area.equals(donorData.area.trim(), ignoreCase = true)
                                    ) {
                                        list.add(req!!.copy(requestId = child.key ?: ""))
                                    }
                                }
                            }

                            matchingRequests = list
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Donor Notifications") })
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    // Section 1: Blood Requests
                    Text("Matching Approved Requests", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (matchingRequests.isEmpty()) {
                        Text("No matching requests found.")
                    } else {
                        LazyColumn {
                            items(matchingRequests) { req ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Blood Group: ${req.bloodGroup}")
                                        Text("Area: ${req.area}")
                                        Text("Date: ${req.date}")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section 2: Admin Notifications
                    Text("Admin Messages", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (adminNotifications.isEmpty()) {
                        Text("No admin messages found.")
                    } else {
                        LazyColumn {
                            items(adminNotifications) { msg ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(" ${msg.title}")
                                        Text(msg.message)
                                        Text(" ${msg.timestamp}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
