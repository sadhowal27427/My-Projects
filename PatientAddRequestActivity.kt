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
import com.example.vublooddonationsociety.models.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PatientAddRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddBloodRequestScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodRequestScreen() {
    val context = LocalContext.current
    var bloodGroup by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Blood Request Form", style = MaterialTheme.typography.headlineSmall)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { },
                readOnly = true,
                label = { Text("Select Blood Group") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Area") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (e.g. yyyy-mm-dd)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid.isNullOrEmpty()) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val requestId = FirebaseDatabase.getInstance().reference.push().key ?: ""
                val request = BloodRequest(
                    userId = uid,
                    requestId = requestId,
                    bloodGroup = bloodGroup.trim(),
                    area = area.trim(),
                    date = date.trim(),
                    status = "Pending",
                    approved = false
                )

                FirebaseDatabase.getInstance().getReference("requests")
                    .child(uid)
                    .child(requestId)
                    .setValue(request)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Request submitted", Toast.LENGTH_SHORT).show()
                        bloodGroup = ""
                        area = ""
                        date = ""
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to submit request", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Request")
        }
    }
}
