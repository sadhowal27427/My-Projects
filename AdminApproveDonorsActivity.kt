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
import com.google.firebase.database.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext




class AdminApproveDonorsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("donors")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val pendingDonors = remember { mutableStateListOf<DonorRequest>() }

            val dbRef = FirebaseDatabase.getInstance().getReference("donors")

            LaunchedEffect(Unit) {
                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<DonorRequest>()
                        for (child in snapshot.children) {
                            val donor = child.getValue(DonorRequest::class.java)
                            if (donor != null && donor.approved == false) {
                                list.add(donor.copy(uid = child.key ?: ""))
                            }
                        }
                        pendingDonors.clear()
                        pendingDonors.addAll(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AdminApproveDonorsActivity,
                            "Failed to load donors: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = { Text("Approve Donor Registrations") })
                }
            ) { innerPadding ->
                if (pendingDonors.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pending donors found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        items(pendingDonors) { donor ->
                            DonorCard(donor, pendingDonors)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DonorCard(donor: DonorRequest, list: SnapshotStateList<DonorRequest>) {
    val dbRef = FirebaseDatabase.getInstance().getReference("donors")
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${donor.name}")
            Text("Email: ${donor.email}")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    dbRef.child(donor.uid).child("approved").setValue(true)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Donor Approved", Toast.LENGTH_SHORT).show()
                            list.remove(donor)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Approval Failed", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Approve")
                }

                Button(onClick = {
                    dbRef.child(donor.uid).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Donor Deleted", Toast.LENGTH_SHORT).show()
                            list.remove(donor)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Delete")
                }
            }
        }
    }
}
