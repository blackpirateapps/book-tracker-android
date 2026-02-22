package com.booktracker.app.data.repository

import com.booktracker.app.data.preferences.ThemePreferences
import com.booktracker.app.data.remote.dto.BookDto
import com.booktracker.app.data.remote.dto.ImageLinksDto
import com.booktracker.app.data.remote.dto.SearchBookDto
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.SearchBook
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.repository.BookRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class KtorBookRepository(
    private val themePreferences: ThemePreferences
) : BookRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val baseUrl: String
        get() = themePreferences.apiDomain.trimEnd('/')

    private val password: String
        get() = themePreferences.apiPassword

    override suspend fun getBooks(): List<Book> {
        return try {
            val response: List<BookDto> = client.get("$baseUrl/api/books").body()
            response.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBookById(id: String): Book? {
        return try {
            val response: BookDto = client.get("$baseUrl/api/books") {
                parameter("id", id)
            }.body()
            response.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addBook(book: Book) {
        val payload = buildJsonObject {
            put("olid", book.id)
            put("shelf", book.shelf.apiValue)
        }
        performAction("add", payload)
    }

    override suspend fun updateBook(book: Book) {
        val payload = buildJsonObject {
            put("id", book.id)
            put("shelf", book.shelf.apiValue)
            put("readingProgress", book.progress)
            put("readingMedium", book.readingMedium)
            book.finishedOn?.let { put("finishedOn", it) }
            book.startedOn?.let { put("startedOn", it) }
            put("title", book.title)
            put("bookDescription", book.description)
            putJsonArray("authors") {
                book.authors.forEach { add(it) }
            }
        }
        performAction("update", payload)
    }

    override suspend fun deleteBook(id: String) {
        performAction("delete", buildJsonObject { put("id", id) })
    }

    private suspend fun performAction(action: String, data: JsonElement) {
        try {
            client.post("$baseUrl/api/books") {
                contentType(ContentType.Application.Json)
                setBody(ActionRequest(password, action, data))
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    @Serializable
    private data class ActionRequest(
        val password: String,
        val action: String,
        val data: JsonElement
    )

    private fun BookDto.toDomain(): Book {
        val authorsList = authors.asStringList(json)
        val shelfType = ShelfType.values().find { it.apiValue == shelf } ?: ShelfType.READING_LIST

        return Book(
            id = id,
            title = title,
            author = authorsList.firstOrNull() ?: "Unknown",
            authors = authorsList,
            shelf = shelfType,
            progress = readingProgress,
            coverUrl = imageLinks.asImageLinks(json)?.thumbnail ?: "",
            description = bookDescription,
            pageCount = pageCount,
            readingMedium = readingMedium,
            startedOn = startedOn,
            finishedOn = finishedOn,
            highlights = highlights.asStringList(json),
            tags = tags.asStringList(json),
            subjects = subjects.asStringList(json),
            publisher = publisher,
            publishDate = fullPublishDate ?: publishedDate
        )
    }

    private fun JsonElement?.asStringList(json: Json): List<String> {
        if (this == null) return emptyList()
        return when (this) {
            is JsonArray -> this.mapNotNull { it.jsonPrimitive.contentOrNull }
            is JsonPrimitive -> {
                val content = this.contentOrNull ?: return emptyList()
                parseStringList(content, json)
            }
            else -> emptyList()
        }
    }

    private fun JsonElement?.asImageLinks(json: Json): ImageLinksDto? {
        if (this == null) return null
        return when (this) {
            is JsonObject -> json.decodeFromJsonElement(this)
            is JsonPrimitive -> {
                val content = this.contentOrNull ?: return null
                val parsed = parseJson(content, json) ?: return null
                if (parsed is JsonObject) json.decodeFromJsonElement(parsed) else null
            }
            else -> null
        }
    }

    private fun parseStringList(raw: String, json: Json): List<String> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return emptyList()
        val parsed = parseJson(trimmed, json)
        return if (parsed is JsonArray) {
            parsed.mapNotNull { it.jsonPrimitive.contentOrNull }
        } else {
            listOf(trimmed)
        }
    }

    private fun parseJson(raw: String, json: Json): JsonElement? {
        return try {
            json.parseToJsonElement(raw)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun testConnection(): Result<Boolean> {
        return try {
            val response = client.get("$baseUrl/api/public") {
                parameter("limit", 1)
            }
            if (response.status.isSuccess()) {
                Result.success(true)
            } else {
                Result.failure(Exception("Server returned ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchRawPublic(limit: Int, offset: Int): Result<String> {
        return try {
            val response = client.get("$baseUrl/api/public") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Server returned ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchRawBooks(): Result<String> {
        return try {
            val response = client.get("$baseUrl/api/books")
            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Server returned ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchBooks(query: String): Result<List<SearchBook>> {
        return try {
            val response: List<SearchBookDto> = client.get("$baseUrl/api/search") {
                parameter("q", query)
            }.body()
            val results = response.mapNotNull { dto ->
                val key = dto.key ?: return@mapNotNull null
                SearchBook(
                    id = key,
                    title = dto.title ?: "Untitled",
                    author = dto.author_name?.firstOrNull() ?: "Unknown",
                    publishYear = dto.first_publish_year,
                    coverUrl = dto.cover_i?.let { coverId ->
                        "https://covers.openlibrary.org/b/id/${coverId}-M.jpg"
                    }
                )
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addBookByOlid(olid: String, shelfApiValue: String): Result<Boolean> {
        val payload = buildJsonObject {
            put("olid", olid)
            put("shelf", shelfApiValue)
        }
        return try {
            client.post("$baseUrl/api/books") {
                contentType(ContentType.Application.Json)
                setBody(ActionRequest(password, "add", payload))
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
