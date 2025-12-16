package com.example.vublooddonationsociety.models

data class BloodRequest(
    val userId: String = "",
    val requestId: String = "",
    val bloodGroup: String = "",
    val date: String = "",
    val area: String = "",
    val status: String = "Pending",
    val approved: Boolean = false
)