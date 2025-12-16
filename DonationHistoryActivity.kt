package com.example.vublooddonationsociety

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vublooddonationsociety.model.Donation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DonationHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonationHistoryScreen()
        }
    }
}

@Composable
fun DonationHistoryScreen() {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference
    var donationHistory by remember { mutableStateOf<List<Donation>>(emptyList()) }

    LaunchedEffect(uid) {
        uid?.let {
            database.child("donors").child(it).child("donationHistory")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<Donation>()
                        for (donationSnapshot in snapshot.children) {
                            val donation = donationSnapshot.getValue(Donation::class.java)
                            donation?.let { list.add(it) }
                        }
                        donationHistory = list
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to load history: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Donation History", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (donationHistory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No donations found.")
            }
        } else {
            LazyColumn {
                items(donationHistory) { donation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Date: ${donation.date.ifBlank { "N/A" }}")
                            Text("Location: ${donation.location.ifBlank { "N/A" }}")
                            Text("Notes: ${donation.notes.ifBlank { "None" }}")
                        }
                    }
                }
            }
        }
    }
}
