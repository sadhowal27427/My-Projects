package com.example.vublooddonationsociety

import android.os.Bundle
import android.widget.Toast
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

class PatientRequestsActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("requests")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var requests by remember { mutableStateOf<List<BloodRequest>>(emptyList()) }

            val userId = auth.currentUser?.uid ?: ""

            LaunchedEffect(true) {
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<BloodRequest>()
                        for (userNode in snapshot.children) {
                            for (requestNode in userNode.children) {
                                val request = requestNode.getValue(BloodRequest::class.java)
                                if (request != null && request.userId == userId) {
                                    val updatedRequest = request.copy(
                                        requestId = requestNode.key ?: "",
                                        approved = requestNode.child("approved").getValue(Boolean::class.java) ?: false
                                    )
                                    list.add(updatedRequest)
                                }
                            }
                        }
                        requests = list
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@PatientRequestsActivity,
                            "Error loading requests",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("My Blood Requests") }) }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    items(requests) { request ->
                        RequestItem(request)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun RequestItem(request: BloodRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Request ID: ${request.requestId}")
                Text("Blood Group: ${request.bloodGroup}")
                Text("Area: ${request.area}")
                Text("Date: ${request.date}")
                Text("Status: ${if (request.approved) "approved" else "Pending"}")
            }
        }
    }
}
