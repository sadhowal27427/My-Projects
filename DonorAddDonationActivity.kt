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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class DonorAddDonationActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DonorAddDonationScreen { date, area, bloodGroup ->
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val donationId = database.child("donations").child(uid).push().key ?: UUID.randomUUID().toString()
                    val donationData = mapOf(
                        "date" to date,
                        "area" to area,
                        "bloodGroup" to bloodGroup
                    )
                    database.child("donations").child(uid).child(donationId).setValue(donationData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Donation submitted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}

@Composable
fun DonorAddDonationScreen(onSubmit: (String, String, String) -> Unit) {
    var area by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }

    val date = remember {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Submit Donation", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Area") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSubmit(date, area, bloodGroup) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
