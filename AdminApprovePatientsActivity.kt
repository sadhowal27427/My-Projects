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
import androidx.compose.ui.Alignment
import com.google.firebase.database.*
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Patient(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val approved: Boolean = false
)

class AdminApprovePatientsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("patients")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val patients = remember { mutableStateListOf<Patient>() }

            LaunchedEffect(Unit) {
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<Patient>()
                        for (child in snapshot.children) {
                            val patient = child.getValue(Patient::class.java)
                            if (patient != null && patient.approved == false) {
                                list.add(patient.copy(uid = child.key ?: ""))
                            }
                        }
                        patients.clear()
                        patients.addAll(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AdminApprovePatientsActivity,
                            "Failed to load patients: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = { Text("Approve Patient Registrations") })
                }
            ) { padding ->
                if (patients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pending patients found.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                        items(patients) { patient ->
                            PatientCard(patient, patients)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PatientCard(patient: Patient, list: SnapshotStateList<Patient>) {
        val dbRef = FirebaseDatabase.getInstance().getReference("patients")

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Name: ${patient.name}")
                Text("Email: ${patient.email}")
                Text("Blood Group: ${patient.bloodGroup}")
                Text("Location: ${patient.location}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        dbRef.child(patient.uid).child("approved").setValue(true)
                            .addOnSuccessListener {
                                Toast.makeText(this@AdminApprovePatientsActivity, "Approved", Toast.LENGTH_SHORT).show()
                                list.remove(patient)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@AdminApprovePatientsActivity, "Error updating", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Text("Approve")
                    }

                    Button(onClick = {
                        dbRef.child(patient.uid).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@AdminApprovePatientsActivity, "Deleted", Toast.LENGTH_SHORT).show()
                                list.remove(patient)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@AdminApprovePatientsActivity, "Error deleting", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
