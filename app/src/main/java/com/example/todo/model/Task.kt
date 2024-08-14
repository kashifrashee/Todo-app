package com.example.todo.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isFavourite: Boolean = false
)
