package com.example.clientapp.models

import java.util.*

data class Message(
    val id: Int,
    var from: String,
    var to: String,
    var message: String?,
    var time: String
)