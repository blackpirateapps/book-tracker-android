package com.booktracker.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchBookDto(
    val key: String? = null,
    val title: String? = null,
    val author_name: List<String>? = null,
    val first_publish_year: Int? = null,
    val cover_i: Int? = null
)
