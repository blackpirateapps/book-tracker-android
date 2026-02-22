package com.booktracker.app.domain.repository

import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.SearchBook

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBookById(id: String): Book?
    suspend fun addBook(book: Book)
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(id: String)
    suspend fun testConnection(): Result<Boolean>
    suspend fun fetchRawPublic(limit: Int = 3, offset: Int = 0): Result<String>
    suspend fun fetchRawBooks(): Result<String>
    suspend fun searchBooks(query: String): Result<List<SearchBook>>
    suspend fun addBookByOlid(olid: String, shelfApiValue: String): Result<Boolean>
}
