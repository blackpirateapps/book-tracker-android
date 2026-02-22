package com.booktracker.app.data.repository

import com.booktracker.app.data.datasource.MockBookDataSource
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.repository.BookRepository

class MockBookRepository : BookRepository {

    private val books = MockBookDataSource.getBooks().toMutableList()

    override suspend fun getBooks(): List<Book> {
        return books.toList()
    }

    override suspend fun getBookById(id: String): Book? {
        return books.find { it.id == id }
    }

    override suspend fun addBook(book: Book) {
        books.add(book)
    }

    override suspend fun updateBook(book: Book) {
        val index = books.indexOfFirst { it.id == book.id }
        if (index != -1) {
            books[index] = book
        }
    }

    override suspend fun deleteBook(id: String) {
        books.removeAll { it.id == id }
    }
}
