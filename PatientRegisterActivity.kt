package com.example.vublooddonationsociety

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
class PatientRegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var bloodGroup by remember { mutableStateOf("A+") }
            var location by remember { mutableStateOf("") }
            var expanded by remember { mutableStateOf(false) }
            val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Patient Registration") }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Blood Group") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            bloodGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group) },
                                    onClick = {
                                        bloodGroup = group
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            registerPatient(name, email, password, bloodGroup, location)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }

    private fun registerPatient(
        name: String,
        email: String,
        password: String,
        bloodGroup: String,
        location: String
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || bloodGroup.isBlank() || location.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                val patient = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "bloodGroup" to bloodGroup,
                    "location" to location,
                    "approved" to false
                )

                FirebaseDatabase.getInstance().reference
                    .child("patients")
                    .child(uid)
                    .setValue(patient)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration successful. Awaiting admin approval.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, PatientLoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save patient info.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
