package com.example.clientapp.models

import java.util.*

data class Message(
    val id: Int,
    var from: Int?,
    var to: Int?,
    var message: String?,
    var date: Date
)