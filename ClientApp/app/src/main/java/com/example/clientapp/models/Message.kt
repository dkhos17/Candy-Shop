package com.example.clientapp.models

data class Message(
    val id: Int,
    var from: Int?,
    var to: Int?,
    var message: String?
)