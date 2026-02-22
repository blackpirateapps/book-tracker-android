package com.booktracker.app.data.datasource

import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.ShelfType

object MockBookDataSource {

    fun getBooks(): List<Book> = listOf(
        Book(
            id = "1",
            title = "Project Hail Mary",
            author = "Andy Weir",
            shelf = ShelfType.READING,
            progress = 67,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "2",
            title = "Atomic Habits",
            author = "James Clear",
            shelf = ShelfType.READ,
            progress = 100,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "3",
            title = "Dune",
            author = "Frank Herbert",
            shelf = ShelfType.READ,
            progress = 100,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "4",
            title = "The Midnight Library",
            author = "Matt Haig",
            shelf = ShelfType.READING,
            progress = 34,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "5",
            title = "Sapiens",
            author = "Yuval Noah Harari",
            shelf = ShelfType.ABANDONED,
            progress = 22,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "6",
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            shelf = ShelfType.READING_LIST,
            progress = 0,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "7",
            title = "Educated",
            author = "Tara Westover",
            shelf = ShelfType.READING,
            progress = 85,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        ),
        Book(
            id = "8",
            title = "1984",
            author = "George Orwell",
            shelf = ShelfType.READING_LIST,
            progress = 0,
            coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
        )
    )
}
