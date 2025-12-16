package com.example.vublooddonationsociety.models

data class PatientRequest(
    val requestId: String = "",
    val userId: String = "",
    val bloodGroup: String = "",
    val date: String = "",
    val area: String = "",
    val approved: Boolean = false
)
