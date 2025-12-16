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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.models.BloodRequest
import com.google.firebase.database.*

class AdminVerifyBloodRequestsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("requests")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var requests by remember {
                mutableStateOf<List<Pair<String, BloodRequest>>>(emptyList())
            }

            LaunchedEffect(Unit) {
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<Pair<String, BloodRequest>>()
                        for (userNode in snapshot.children) {
                            for (child in userNode.children) {
                                val request = child.getValue(BloodRequest::class.java)
                                val fullPath = "${userNode.key}/${child.key}"
                                if (request != null && !request.approved) {
                                    list.add(Pair(fullPath, request))
                                }
                            }
                        }
                        requests = list
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AdminVerifyBloodRequestsActivity,
                            "Error loading requests",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            AdminVerifyRequestsScreen(
                requests = requests,
                onApprove = { fullPath ->
                    val ref = database.child(fullPath)
                    ref.child("approved").setValue(true)
                    ref.child("status").setValue("approved")
                        .addOnSuccessListener {
                            Toast.makeText(this, "Request Approved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Approval Failed", Toast.LENGTH_SHORT).show()
                        }
                },
                onDelete = { fullPath ->
                    database.child(fullPath).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Request Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerifyRequestsScreen(
    requests: List<Pair<String, BloodRequest>>,
    onApprove: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Approve Blood Requests") })
        }
    ) { padding ->
        if (requests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No pending requests")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(requests) { (key, request) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("User ID: ${request.userId}")
                            Text("Blood Group: ${request.bloodGroup}")
                            Text("Area: ${request.area}")
                            Text("Date: ${request.date}")
                            Text("Status: ${request.status}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onApprove(key) }) {
                                    Text("Approve")
                                }
                                OutlinedButton(onClick = { onDelete(key) }) {
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
