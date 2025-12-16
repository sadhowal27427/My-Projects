package com.example.vublooddonationsociety

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminDashboardScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminDashboardScreen() {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Admin Dashboard") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    startActivity(Intent(this@AdminDashboardActivity, AdminApproveDonorsActivity::class.java))
                }) {
                    Text("View Pending Donor Approvals")
                }

                Button(onClick = {
                    startActivity(Intent(this@AdminDashboardActivity, AdminApprovePatientsActivity::class.java))
                }) {
                    Text("View Pending Patient Approvals")
                }

                Button(onClick = {
                    startActivity(Intent(this@AdminDashboardActivity, AdminViewApprovedDonorsActivity::class.java))
                }) {
                    Text("View Approved Donors")
                }

                Button(onClick = {
                    startActivity(Intent(this@AdminDashboardActivity, AdminViewApprovedPatientsActivity::class.java))
                }) {
                    Text("View Approved Patients")
                }

                Button(onClick = {
                    startActivity(Intent(this@AdminDashboardActivity, AdminVerifyBloodRequestsActivity::class.java))
                }) {
                    Text("Verify Blood Requests")
                }

                Button(
                    onClick = {
                        startActivity(Intent(this@AdminDashboardActivity, AdminViewApprovedRequestsActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Approved Blood Requests")
                }

                Button(
                    onClick = {
                        startActivity(Intent(this@AdminDashboardActivity, AdminSendNotificationActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Notification")
                }

                Button(
                    onClick = {
                        startActivity(Intent(this@AdminDashboardActivity, AdminUpdateProfileActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Profile")
                }

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this@AdminDashboardActivity, AdminLoginActivity::class.java))
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}
