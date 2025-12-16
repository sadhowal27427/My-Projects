package com.example.vublooddonationsociety

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.ui.theme.VUBloodDonationSocietyTheme
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PatientUpdateProfileActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VUBloodDonationSocietyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PatientUpdateProfileScreen()
                }
            }
        }
    }

    @Composable
    fun PatientUpdateProfileScreen() {
        val context = LocalContext.current
        var newName by remember { mutableStateOf("") }
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val user = auth.currentUser
                    val email = user?.email

                    if (user != null && email != null) {
                        val credential = EmailAuthProvider.getCredential(email, currentPassword)
                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                if (newPassword.isNotEmpty()) {
                                    user.updatePassword(newPassword)
                                        .addOnSuccessListener {
                                            // Update name in database
                                            database.child("patients").child(user.uid)
                                                .child("name").setValue(newName)
                                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Password update failed", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // Only update name
                                    database.child("patients").child(user.uid)
                                        .child("name").setValue(newName)
                                    Toast.makeText(context, "Name updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Reauthentication failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Profile")
            }
        }
    }
}
