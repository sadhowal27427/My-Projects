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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.ui.theme.VUBloodDonationSocietyTheme
import com.google.firebase.auth.FirebaseAuth

class AdminLoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            VUBloodDonationSocietyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdminLoginScreen { email, password ->
                        loginAdmin(email, password)
                    }
                }
            }
        }
    }

    private fun loginAdmin(email: String, password: String) {
        val allowedAdminEmail = "admin@vublood.com" // <-- Make sure this matches your Firebase admin email

        if (email != allowedAdminEmail) {
            Toast.makeText(this, "Unauthorized: Not an admin", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // ✅ Navigate to Admin Dashboard
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}

@Composable
fun AdminLoginScreen(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onLogin(email, password) }) {
            Text("Login")
        }
    }
}
