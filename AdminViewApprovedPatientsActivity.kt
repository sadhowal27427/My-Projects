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



class AdminViewApprovedPatientsActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("patients")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var patients by remember { mutableStateOf(listOf<Patient>()) }

            LaunchedEffect(true) {
                database.orderByChild("approved").equalTo(true)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = mutableListOf<Patient>()
                            for (child in snapshot.children) {
                                val patient = child.getValue(Patient::class.java)
                                if (patient != null) {
                                    list.add(patient.copy(uid = child.key ?: ""))
                                }
                            }
                            patients = list
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Failed to load patients", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("Approved Patients") }) }
            ) { padding ->
                LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                    items(patients) { patient ->
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Name: ${patient.name}")
                                Text("Email: ${patient.email}")
                                Text("Blood Group: ${patient.bloodGroup}")
                                Text("Location: ${patient.location}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    FirebaseDatabase.getInstance().getReference("patients")
                                        .child(patient.uid).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Patient deleted", Toast.LENGTH_SHORT).show()
                                            patients = patients.filterNot { it.uid == patient.uid }
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
