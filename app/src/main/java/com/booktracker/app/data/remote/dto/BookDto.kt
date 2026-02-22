package com.booktracker.app.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val authors: String? = "[]", // JSON array
    val imageLinks: String? = "{}", // JSON object
    val pageCount: Int? = null,
    val publishedDate: String? = null,
    val fullPublishDate: String? = null,
    val publisher: String? = null,
    val industryIdentifiers: String? = "[]", // JSON array
    val highlights: String? = "[]", // JSON array
    val startedOn: String? = null,
    val finishedOn: String? = null,
    val readingMedium: String = "Not set",
    val shelf: String,
    val hasHighlights: Int = 0,
    val readingProgress: Int = 0,
    val bookDescription: String? = null,
    val subjects: String? = "[]", // JSON array
    val tags: String? = "[]" // JSON array
)

@Serializable
data class ImageLinksDto(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

// Helper extension to parse JSON fields
fun String?.parseAsList(): List<String> {
    if (this == null || this.isBlank() || this == "null") return emptyList()
    return try {
        Json.decodeFromString<List<String>>(this)
    } catch (e: Exception) {
        emptyList()
    }
}

fun String?.parseAsImageLinks(): ImageLinksDto {
    if (this == null || this.isBlank() || this == "{}" || this == "null") return ImageLinksDto()
    return try {
        Json.decodeFromString<ImageLinksDto>(this)
    } catch (e: Exception) {
        ImageLinksDto()
    }
}
