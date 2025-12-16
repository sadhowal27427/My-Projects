package com.example.vublooddonationsociety

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vublooddonationsociety.ui.theme.VUBloodDonationSocietyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientProfileActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VUBloodDonationSocietyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PatientProfileScreen()
                }
            }
        }
    }

    @Composable
    fun PatientProfileScreen() {
        val context = LocalContext.current
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        val uid = auth.currentUser?.uid

        // Load patient data
        LaunchedEffect(uid) {
            if (uid != null) {
                val patientRef = database.child("patients").child(uid)
                patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        name = snapshot.child("name").getValue(String::class.java) ?: ""
                        email = snapshot.child("email").getValue(String::class.java) ?: ""
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Patient Profile",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Name: $name", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: $email", fontSize = 18.sp)


            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    try {
                        context.startActivity(Intent(context, PatientAddRequestActivity::class.java))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("New Blood Request")
            }

            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {
                    context.startActivity(Intent(context, PatientRequestsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View My Requests")
            }



            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, SearchDonorsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Donors")
            }

            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {
                    try {
                        context.startActivity(Intent(context, PatientUpdateProfileActivity::class.java))
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))


            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
