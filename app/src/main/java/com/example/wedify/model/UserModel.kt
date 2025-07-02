package com.example.wedify.model

data class UserModel(
    val uid: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val telephone: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val photoUrl: String = "", // ← Tambahkan ini
    val cartItems: Map<String, Long> = emptyMap(),
    val role: String = "user"
)
