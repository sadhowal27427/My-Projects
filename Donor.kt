package com.example.vublooddonationsociety.model

data class Donor(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val approved: Boolean = false,
    val donationHistory: Map<String, Donation> = emptyMap()
)
