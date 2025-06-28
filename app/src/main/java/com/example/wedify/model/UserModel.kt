package com.example.wedify.model

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val telephone: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val cartItems: Map<String, Long> = emptyMap(),
    val role: String = "user"
)
