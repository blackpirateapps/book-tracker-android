package com.booktracker.app.domain.model

data class SearchBook(
    val id: String,
    val title: String,
    val author: String,
    val publishYear: Int? = null,
    val coverUrl: String? = null
)
