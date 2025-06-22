package com.example.wedify.model

data class BookingModel(
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val status: String = "pending",
    val buktiTransferUrl: String = "",
    val total: Float = 0f // hanya total harga akhir
)



