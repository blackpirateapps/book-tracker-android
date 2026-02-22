package com.booktracker.app.domain.model

import java.util.UUID

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String, // Kept as singular for UI convenience, mapping from authors[0]
    val authors: List<String> = emptyList(),
    val shelf: ShelfType,
    val progress: Int = 0,
    val coverUrl: String = "",
    val description: String? = null,
    val pageCount: Int? = null,
    val readingMedium: String = "Not set",
    val startedOn: String? = null,
    val finishedOn: String? = null,
    val highlights: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val subjects: List<String> = emptyList(),
    val publisher: String? = null,
    val publishDate: String? = null
)

enum class ShelfType(val apiValue: String, val displayName: String) {
    READING("currentlyReading", "Reading"),
    READ("read", "Read"),
    ABANDONED("abandoned", "Abandoned"),
    READING_LIST("watchlist", "Reading List")
}
