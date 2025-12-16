package com.example.vublooddonationsociety

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AdminSendNotificationActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().reference

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bloodGroup by remember { mutableStateOf("") }
            var area by remember { mutableStateOf("") }
            var message by remember { mutableStateOf("") }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Text("Send Notification", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Blood Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
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
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        sendNotifications(bloodGroup.trim(), area.trim(), message.trim())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Notification")
                }
            }
        }
    }

    private fun sendNotifications(bloodGroup: String, area: String, message: String) {
        if (bloodGroup.isEmpty() || area.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val donorsRef = database.child("donors")
        donorsRef.get().addOnSuccessListener { snapshot ->
            var count = 0
            for (donorSnap in snapshot.children) {
                val donorData = donorSnap.value as? Map<*, *> ?: continue

                val donorBloodGroup = (donorData["bloodGroup"] as? String)?.trim() ?: ""
                val donorArea = (donorData["area"] as? String)?.trim() ?: ""
                val approved = donorData["approved"] as? Boolean ?: false

                if (
                    donorBloodGroup.equals(bloodGroup.trim(), ignoreCase = true) &&
                    donorArea.equals(area.trim(), ignoreCase = true)
                    && approved
                ) {
                    val donorUid = donorSnap.key ?: continue
                    val notificationId = database.child("notifications").child(donorUid).push().key ?: continue

                    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                    val notificationData = mapOf(
                        "title" to "Blood Request",
                        "message" to message,
                        "timestamp" to timestamp
                    )

                    database.child("notifications").child(donorUid).child(notificationId).setValue(notificationData)
                    count++
                }
            }

            Toast.makeText(this, "Sent to $count donor(s)", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to send notifications", Toast.LENGTH_SHORT).show()
        }
    }
}
