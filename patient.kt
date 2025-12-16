package com.example.vublooddonationsociety.model

data class Patient(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bloodGroup: String = "",
    val location: String = "",
    val approved: Boolean = false
)


