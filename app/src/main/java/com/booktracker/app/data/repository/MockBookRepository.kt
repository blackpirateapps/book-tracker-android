package com.booktracker.app.data.repository

import com.booktracker.app.data.datasource.MockBookDataSource
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.SearchBook
import com.booktracker.app.domain.repository.BookRepository

class MockBookRepository : BookRepository {

    private val books = MockBookDataSource.getBooks().toMutableList()

    override suspend fun getBooks(): List<Book> {
        return books.toList()
    }

    override suspend fun getBookById(id: String): Book? {
        return books.find { it.id == id }
    }

    override suspend fun addBook(book: Book): Result<Boolean> {
        books.add(book)
        return Result.success(true)
    }

    override suspend fun updateBook(book: Book): Result<Boolean> {
        val index = books.indexOfFirst { it.id == book.id }
        if (index != -1) {
            books[index] = book
            return Result.success(true)
        }
        return Result.failure(Exception("Book not found"))
    }

    override suspend fun deleteBook(id: String): Result<Boolean> {
        val removed = books.removeAll { it.id == id }
        return if (removed) Result.success(true) else Result.failure(Exception("Book not found"))
    }

    override suspend fun testConnection(): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun fetchRawPublic(limit: Int, offset: Int): Result<String> {
        return Result.success("[]")
    }

    override suspend fun fetchRawBooks(): Result<String> {
        return Result.success("[]")
    }

    override suspend fun searchBooks(query: String): Result<List<SearchBook>> {
        return Result.success(emptyList())
    }

    override suspend fun addBookByOlid(olid: String, shelfApiValue: String): Result<Boolean> {
        return Result.success(true)
    }
}
