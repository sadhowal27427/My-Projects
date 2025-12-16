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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

data class DonorRequest(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val area: String = "",
    val approved: Boolean = false
)

class AdminViewApprovedDonorsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("donors")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var donors by remember { mutableStateOf(listOf<DonorRequest>()) }

            LaunchedEffect(true) {
                database.orderByChild("approved").equalTo(true)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = mutableListOf<DonorRequest>()
                            for (child in snapshot.children) {
                                val donor = child.getValue(DonorRequest::class.java)
                                if (donor != null) {
                                    list.add(donor.copy(uid = child.key ?: ""))
                                }
                            }
                            donors = list
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Failed to load donors", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("Approved Donors") }) }
            ) { padding ->
                LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                    items(donors) { donor ->
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Name: ${donor.name}")
                                Text("Email: ${donor.email}")
                                Text("Blood Group: ${donor.bloodGroup}")
                                Text("Area: ${donor.area}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    FirebaseDatabase.getInstance().getReference("donors")
                                        .child(donor.uid).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Donor deleted", Toast.LENGTH_SHORT).show()
                                            donors = donors.filterNot { it.uid == donor.uid }
                                        }
                                }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
