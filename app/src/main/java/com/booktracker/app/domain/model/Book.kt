package com.booktracker.app.domain.model

import java.util.UUID

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val shelf: ShelfType,
    val progress: Int = 0,
    val coverUrl: String = ""
)

enum class ShelfType(val displayName: String) {
    READING("Reading"),
    READ("Read"),
    ABANDONED("Abandoned"),
    READING_LIST("Reading List")
}
