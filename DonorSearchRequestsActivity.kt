package com.example.vublooddonationsociety

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vublooddonationsociety.models.BloodRequest
import com.google.firebase.database.*
import android.util.Log

class DonorSearchRequestsActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        setContent {
            SearchRequestsScreen(database)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRequestsScreen(database: DatabaseReference) {
    var selectedBloodGroup by remember { mutableStateOf("A+") }
    var area by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<BloodRequest>()) }
    var searchPerformed by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Search Patient Requests", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedBloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Blood Group") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
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
            value = area,
            onValueChange = { area = it },
            label = { Text("Enter Area") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                searchPerformed = true
                database.child("requests").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<BloodRequest>()
                        for (parent in snapshot.children) {
                            for (child in parent.children) {
                                try {
                                    val request = child.getValue(BloodRequest::class.java)
                                    if (
                                        request != null &&
                                        request.approved &&
                                        request.bloodGroup == selectedBloodGroup &&
                                        request.area.trim().equals(area.trim(), ignoreCase = true)
                                    ) {
                                        list.add(request)
                                    }
                                } catch (e: Exception) {
                                    Log.e("DONOR_SEARCH", "Error parsing request", e)
                                }
                            }
                        }

                        results = list
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }


        Spacer(modifier = Modifier.height(24.dp))

        if (searchPerformed) {
            if (results.isNotEmpty()) {
                Text("Search Results:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(results) { request ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Blood Group: ${request.bloodGroup}")
                                Text("Area: ${request.area}")
                                Text("Date: ${request.date}")
                            }
                        }
                    }
                }
            } else {
                Text("No matching requests found.")
            }
        }
    }
}
