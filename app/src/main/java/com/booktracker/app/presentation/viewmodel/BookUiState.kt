package com.booktracker.app.presentation.viewmodel

import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.SearchBook
import com.booktracker.app.domain.model.ShelfType

// ─── Home Screen State ───────────────────────────────────

data class HomeUiState(
    val books: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val selectedShelf: ShelfType = ShelfType.READING,
    val searchQuery: String = "",
    val searchResults: List<SearchBook> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val isGridView: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddSheet: Boolean = false
)

sealed class HomeEvent {
    data class OnSearchChanged(val query: String) : HomeEvent()
    data class OnShelfChanged(val shelf: ShelfType) : HomeEvent()
    data class OnBookClicked(val bookId: String) : HomeEvent()
    data object OnAddBookClicked : HomeEvent()
    data object OnDismissAddSheet : HomeEvent()
    data object OnRefresh : HomeEvent()
    data class OnAddFromSearch(val olid: String) : HomeEvent()
    data object OnToggleLayout : HomeEvent()
}

// ─── Book Detail Screen State ────────────────────────────

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateBack: Boolean = false,
    val isEditing: Boolean = false,
    val editTitle: String = "",
    val editAuthor: String = "",
    val editReadingMedium: String = "",
    val editStartedOn: String = "",
    val editFinishedOn: String = "",
    val editPageCount: String = "",
    val editDescription: String = ""
)

sealed class BookDetailEvent {
    data class OnProgressChanged(val progress: Int) : BookDetailEvent()
    data class OnMoveShelf(val shelf: ShelfType) : BookDetailEvent()
    data object OnMarkAsRead : BookDetailEvent()
    data object OnMarkAsAbandoned : BookDetailEvent()
    data object OnBackClicked : BookDetailEvent()
    data object OnEditClicked : BookDetailEvent()
    data object OnSaveClicked : BookDetailEvent()
    data object OnCancelEditClicked : BookDetailEvent()
    data class OnEditTitleChanged(val title: String) : BookDetailEvent()
    data class OnEditAuthorChanged(val author: String) : BookDetailEvent()
    data class OnEditReadingMediumChanged(val value: String) : BookDetailEvent()
    data class OnEditStartedOnChanged(val value: String) : BookDetailEvent()
    data class OnEditFinishedOnChanged(val value: String) : BookDetailEvent()
    data class OnEditPageCountChanged(val value: String) : BookDetailEvent()
    data class OnEditDescriptionChanged(val value: String) : BookDetailEvent()
}

// ─── Add Book Screen State ───────────────────────────────

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val shelf: ShelfType = ShelfType.READING_LIST,
    val progress: Int = 0,
    val titleError: String? = null,
    val authorError: String? = null,
    val isSuccess: Boolean = false
)

sealed class AddBookEvent {
    data class OnTitleChanged(val title: String) : AddBookEvent()
    data class OnAuthorChanged(val author: String) : AddBookEvent()
    data class OnShelfChanged(val shelf: ShelfType) : AddBookEvent()
    data class OnProgressChanged(val progress: Int) : AddBookEvent()
    data object OnAddBook : AddBookEvent()
    data object OnCancel : AddBookEvent()
}
