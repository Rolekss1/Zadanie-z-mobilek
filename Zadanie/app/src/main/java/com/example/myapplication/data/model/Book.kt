package com.example.myapplication.data.model

data class Book(
    val title: String?,
    val key: String,
    val authors: List<Author>?,
    val cover_id: Int?
)
