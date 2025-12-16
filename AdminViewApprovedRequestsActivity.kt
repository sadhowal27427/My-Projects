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
import com.example.vublooddonationsociety.models.PatientRequest
import com.google.firebase.database.*

class AdminViewApprovedRequestsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("        requests")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var approvedRequests by remember { mutableStateOf(listOf<PatientRequest>()) }

            LaunchedEffect(Unit) {
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<PatientRequest>()

                        for (userSnapshot in snapshot.children) {
                            for (requestSnapshot in userSnapshot.children) {
                                val request = requestSnapshot.getValue(PatientRequest::class.java)
                                if (request != null && request.approved == true) {
                                    list.add(request)
                                }
                            }
                        }

                        approvedRequests = list
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AdminViewApprovedRequestsActivity,
                            "Error loading requests",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            AdminApprovedRequestsScreen(approvedRequests)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApprovedRequestsScreen(requests: List<PatientRequest>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Approved Blood Requests") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(requests) { request ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Request ID: ${request.requestId}")
                        Text("Blood Group: ${request.bloodGroup}")
                        Text("Area: ${request.area}")
                        Text("Status: Approved")
                        if (request.date.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Date: ${request.date}")
                        }
                    }
                }
            }
        }
    }
}
