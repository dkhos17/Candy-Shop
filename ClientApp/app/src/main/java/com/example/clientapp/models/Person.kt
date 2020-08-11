package com.example.clientapp.models

import java.io.Serializable

data class Person(
    var user: User,
    var messages: MutableList<Message>
) : Serializable