package com.example.vublooddonationsociety

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vublooddonationsociety.model.Donation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DonorProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonorProfileScreen()
        }
    }
}

@Composable
fun DonorProfileScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser?.uid

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isActive by remember { mutableStateOf(true) }

    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Load donor data
    LaunchedEffect(uid) {
        uid?.let {
            database.child("donors").child(uid).get().addOnSuccessListener { snapshot ->
                name = snapshot.child("name").value.toString()
                email = snapshot.child("email").value.toString()
                isActive = snapshot.child("isActive").value as? Boolean ?: true
                isLoading = false
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (isLoading) {
        Text("Loading...", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Donor Profile",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text("Name: $name", fontSize = 18.sp)
            Text("Email: $email", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Status Toggle
            Text("Status: ${if (isActive) "Active" else "Sleeping"}", fontSize = 18.sp)
            Switch(
                checked = isActive,
                onCheckedChange = { checked ->
                    isActive = checked
                    uid?.let {
                        database.child("donors").child(it).child("isActive").setValue(checked)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider()

            Text("Add New Donation", fontSize = 20.sp)

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (e.g., YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    uid?.let {
                        val newDonation = Donation(date = date, location = location, notes = notes)
                        val donationKey = database.child("donors").child(uid).child("donationHistory").push().key
                        if (donationKey != null) {
                            database.child("donors").child(uid).child("donationHistory").child(donationKey)
                                .setValue(newDonation)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Donation added", Toast.LENGTH_SHORT).show()
                                    date = ""
                                    location = ""
                                    notes = ""
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add donation", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Donation")
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, DonorNotificationsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notifications")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, DonationHistoryActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Donation History")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, DonorSearchRequestsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Patient Requests")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, DonorUpdateProfileActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Profile")
            }

            Button(
                onClick = {
                    auth.signOut()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
