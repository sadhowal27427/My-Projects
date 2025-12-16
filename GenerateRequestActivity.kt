package com.example.vublooddonationsociety

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class GenerateRequestActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenerateRequestScreen { bloodGroup, date, city, message ->
                saveBloodRequest(bloodGroup, date, city, message)
            }
        }
    }

    private fun saveBloodRequest(
        bloodGroup: String,
        date: String,
        city: String,
        message: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val requestId = UUID.randomUUID().toString()

        val requestData = mapOf(
            "requestId" to requestId,
            "patientId" to uid,
            "bloodGroup" to bloodGroup,
            "date" to date,
            "city" to city,
            "message" to message,
            "status" to "pending"
        )

        database.reference.child("bloodRequests").child(requestId).setValue(requestData)
            .addOnSuccessListener {
                Toast.makeText(this, "Request submitted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun GenerateRequestScreen(onSubmit: (String, String, String, String) -> Unit) {
    var bloodGroup by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generate Blood Request", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group (e.g. A+, B-)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Required Date (e.g. 2025-06-15)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City / Area") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Additional Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (bloodGroup.isNotEmpty() && date.isNotEmpty() && city.isNotEmpty()) {
                    onSubmit(bloodGroup, date, city, message)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Request")
        }
    }
}
