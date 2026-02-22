package com.booktracker.app.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val authors: JsonElement? = null,
    val imageLinks: JsonElement? = null,
    val pageCount: Int? = null,
    val publishedDate: String? = null,
    val fullPublishDate: String? = null,
    val publisher: String? = null,
    val highlights: JsonElement? = null,
    val startedOn: String? = null,
    val finishedOn: String? = null,
    val readingMedium: String = "Not set",
    val shelf: String,
    val hasHighlights: Int = 0,
    val readingProgress: Int = 0,
    val bookDescription: String? = null,
    val subjects: JsonElement? = null,
    val tags: JsonElement? = null
)

@Serializable
data class ImageLinksDto(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)
