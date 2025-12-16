package com.example.vublooddonationsociety

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.ui.theme.VUBloodDonationSocietyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VUBloodDonationSocietyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FrontPageScreen()
                }
            }
        }
    }

    @Composable
    fun FrontPageScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VU Blood\nDonation Society",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            

            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                },

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Donor Register")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, PatientRegisterActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Patient Register")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Donor Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, PatientLoginActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Patient Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    startActivity(Intent(this@MainActivity, AdminLoginActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Admin Login")
            }
        }
    }
}
