package com.booktracker.app.data.repository

import com.booktracker.app.data.preferences.ThemePreferences
import com.booktracker.app.data.remote.dto.BookDto
import com.booktracker.app.data.remote.dto.parseAsImageLinks
import com.booktracker.app.data.remote.dto.parseAsList
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.repository.BookRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class KtorBookRepository(
    private val themePreferences: ThemePreferences
) : BookRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
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
        // API action: "add" uses olid. For a full Book object update/add, we might need a different flow.
        // Based on API docs, action:"add" takes olid.
        // For now, let's implement update as it's the most common write.
        performAction("add", mapOf("olid" to book.id, "shelf" to book.shelf.apiValue))
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
            // For authors list, we'd need more complex building if it's a list.
            // But let's simplify for now or use Map<String, JsonElement>
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
        val authorsList = authors.parseAsList()
        val images = imageLinks.parseAsImageLinks()
        val shelfType = ShelfType.entries.find { it.apiValue == shelf } ?: ShelfType.READING_LIST

        return Book(
            id = id,
            title = title,
            author = authorsList.firstOrNull() ?: "Unknown",
            authors = authorsList,
            shelf = shelfType,
            progress = readingProgress,
            coverUrl = images.thumbnail ?: "",
            description = bookDescription,
            pageCount = pageCount,
            readingMedium = readingMedium,
            startedOn = startedOn,
            finishedOn = finishedOn,
            highlights = highlights.parseAsList(),
            tags = tags.parseAsList(),
            subjects = subjects.parseAsList(),
            publisher = publisher,
            publishDate = fullPublishDate ?: publishedDate
        )
    }
}
