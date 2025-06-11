package com.example.wedify.model

data class BookingModel(
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val status: String = "pending", // "pending", "paid"
    val buktiTransferUrl: String = ""
)


