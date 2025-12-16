package com.example.vublooddonationsociety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

data class DonorProfile(
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val approved: Boolean = false,
    val isActive: Boolean = true
)

class SearchDonorsActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        setContent {
            SearchDonorsScreen(database)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDonorsScreen(database: DatabaseReference) {
    var selectedBloodGroup by remember { mutableStateOf("A+") }
    var city by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<DonorProfile>()) }
    var expanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Search Donors", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedBloodGroup,
                onValueChange = {}, // Keep it empty because it's readOnly
                readOnly = true,
                label = { Text("Select Blood Group") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor() // This is needed for proper dropdown anchoring
                    .fillMaxWidth(),
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                bloodGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            selectedBloodGroup = group
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter City or Area") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                database.child("donors").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val filtered = mutableListOf<DonorProfile>()
                        for (child in snapshot.children) {
                            val donorMap = child.value as? Map<*, *> ?: continue
                            val donor = DonorProfile(
                                name = donorMap["name"] as? String ?: "",
                                email = donorMap["email"] as? String ?: "",
                                bloodGroup = donorMap["bloodGroup"] as? String ?: "",
                                city = donorMap["city"] as? String ?: "",
                                approved = donorMap["approved"] as? Boolean ?: false,
                                isActive = donorMap["isActive"] as? Boolean ?: true
                            )
                            if (
                                donor.bloodGroup == selectedBloodGroup &&
                                donor.city.contains(city, ignoreCase = true) &&
                                donor.approved &&
                                donor.isActive
                            ) {
                                filtered.add(donor)
                            }
                        }
                        results = filtered
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (results.isNotEmpty()) {
            Text("Matching Donors:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(results) { donor ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${donor.name}")
                            Text("Email: ${donor.email}")
                            Text("Blood Group: ${donor.bloodGroup}")
                            Text("City: ${donor.city}")
                        }
                    }
                }
            }
        } else {
            Text("No matching donors found.")
        }
    }
}
