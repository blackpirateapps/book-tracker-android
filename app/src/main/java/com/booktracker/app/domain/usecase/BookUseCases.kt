package com.booktracker.app.domain.usecase

import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.repository.BookRepository

class GetBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): List<Book> = repository.getBooks()
}

class GetBookByIdUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(id: String): Book? = repository.getBookById(id)
}

class AddBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(book: Book) = repository.addBook(book)
}

class UpdateBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(book: Book) = repository.updateBook(book)
}
