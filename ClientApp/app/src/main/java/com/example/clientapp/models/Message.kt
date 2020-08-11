package com.example.clientapp.models

import java.io.Serializable
import java.util.*

data class Message(
    var from: String,
    var to: String,
    var message: String,
    var date: Date
) : Serializable