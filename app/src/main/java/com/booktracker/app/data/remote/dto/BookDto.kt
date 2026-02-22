package com.booktracker.app.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val authors: List<String>? = emptyList(),
    val imageLinks: ImageLinksDto? = null,
    val pageCount: Int? = null,
    val publishedDate: String? = null,
    val fullPublishDate: String? = null,
    val publisher: String? = null,
    val highlights: List<String>? = emptyList(),
    val startedOn: String? = null,
    val finishedOn: String? = null,
    val readingMedium: String = "Not set",
    val shelf: String,
    val hasHighlights: Int = 0,
    val readingProgress: Int = 0,
    val bookDescription: String? = null,
    val subjects: List<String>? = emptyList(),
    val tags: List<String>? = emptyList()
)

@Serializable
data class ImageLinksDto(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)
