package com.example.clientapp.models

data class Person(
    var user: User,
    var messages: MutableList<Message>
)