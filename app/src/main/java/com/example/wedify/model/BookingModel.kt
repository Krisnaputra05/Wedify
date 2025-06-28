package com.example.wedify.model

data class BookingModel(
    val id: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val status: String = "pending",
    val buktiTransferUrl: String = "",
    val total: Float = 0f,
    val productName: String = "",
    val userId: String = ""
)



