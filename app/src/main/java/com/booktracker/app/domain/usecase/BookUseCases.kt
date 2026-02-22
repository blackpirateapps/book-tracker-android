package com.booktracker.app.domain.usecase

import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.SearchBook
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

class SearchBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(query: String): Result<List<SearchBook>> = repository.searchBooks(query)
}

class AddBookByOlidUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(olid: String, shelfApiValue: String): Result<Boolean> {
        return repository.addBookByOlid(olid, shelfApiValue)
    }
}
