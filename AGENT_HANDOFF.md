# Book Tracker Android App - Agent Handoff Documentation

## Project Overview
Book Tracker is an Android app for managing a personal reading library with support for guest mode that allows users to use their own remote API server. The app is built with Kotlin, Jetpack Compose, and Ktor HTTP client.

### Key Features
- **Guest Mode**: Users can configure a custom API domain and password to fetch/sync books from their own server
- **Book Management**: Add, edit, delete, and organize books into shelves (Reading List, Reading, Read, Abandoned)
- **Search**: Search for books via OpenLibrary API before adding to library
- **Dark Mode**: Toggle between light and dark themes
- **Progress Tracking**: Track reading progress with completion percentage

## Architecture

### Layer Structure
The app follows a clean architecture pattern with distinct layers:

#### 1. **Presentation Layer** (`presentation/`)
- **Screens** (`screens/`): Compose UI screens
  - `HomeScreen.kt`: Main book listing with search, shelf filtering
  - `BookDetailScreen.kt`: Book details, edit metadata, shelf management
  - `AddBookScreen.kt`: Add new books manually
  - `SettingsScreen.kt`: API configuration, theme toggle, connection test
  - `HistoryScreen.kt`: Placeholder for future features

- **ViewModels** (`viewmodel/`): MVVM state management
  - `HomeViewModel`: Manages book listing, search, shelf filtering
  - `BookDetailViewModel`: Handles book details, editing, updates
  - `AddBookViewModel`: Manages adding new books
  - UI States: `HomeUiState`, `BookDetailUiState`, `AddBookUiState` (in `BookUiState.kt`)
  - Events: `HomeEvent`, `BookDetailEvent`, `AddBookEvent`

- **Components** (`components/`): Reusable Compose components
  - `BookCard.kt`: Book grid/list item display
  - `AppBottomNavigation.kt`: Shelf navigation tabs

- **Navigation** (`navigation/`):
  - `AppNavigation.kt`: Composable-based navigation router
  - `Screen.kt`: Route definitions

- **Theme** (`theme/`): Design system
  - `Color.kt`, `Shape.kt`, `Type.kt`, `Theme.kt`

- **Refresh Bus** (`refresh/`):
  - `AppRefreshBus.kt`: Event-based refresh mechanism for real-time updates

#### 2. **Domain Layer** (`domain/`)
Pure Kotlin, no Android dependencies. Contains business logic and contracts.

- **Models** (`model/`):
  - `Book`: Main book entity with shelf, progress, metadata
  - `SearchBook`: Search result from OpenLibrary
  - `ShelfType`: Enum for book categories (READING_LIST, READING, READ, ABANDONED)

- **Repository Interface** (`repository/`):
  - `BookRepository`: Interface for data operations
  - Methods return `Result<Boolean>` for write operations (add/update/delete)
  - Methods return `List<Book>` for read operations

- **Use Cases** (`usecase/`):
  - `GetBooksUseCase`: Fetch all books
  - `AddBookUseCase`: Add new book (returns `Result<Boolean>`)
  - `UpdateBookUseCase`: Update existing book (returns `Result<Boolean>`)
  - `SearchBooksUseCase`: Search via API
  - `AddBookByOlidUseCase`: Add book using OpenLibrary ID

#### 3. **Data Layer** (`data/`)
Handles external data sources and local persistence.

- **Repository Implementation** (`repository/`):
  - `KtorBookRepository`: HTTP client-based implementation using Ktor
  - `MockBookRepository`: In-memory mock for testing
  
- **Remote Data** (`remote/dto/`):
  - `BookDto`: Maps API book responses to domain Book model
  - `SearchBookDto`: OpenLibrary search API response mapping
  - `ImageLinksDto`: Book cover URL mapping

- **Preferences** (`preferences/`):
  - `ThemePreferences`: Stores user settings in SharedPreferences
    - `isDarkModeEnabled`: Theme preference
    - `apiDomain`: Guest mode server URL
    - `apiPassword`: Guest mode API password

- **Data Source** (`datasource/`):
  - `MockBookDataSource`: Test data generation

## Critical Implementation Details

### Guest Mode Architecture
Guest mode allows users to point the app to their own book API server:

1. **Configuration Storage** (ThemePreferences):
   ```kotlin
   var apiDomain: String
   var apiPassword: String
   ```

2. **API Endpoints Used**:
   - `GET /api/books` - Fetch all books
   - `GET /api/books?id=<id>` - Get specific book
   - `POST /api/books` - Create/Update/Delete with action header
   - `GET /api/public` - Public books (no auth)
   - `GET /api/search?q=<query>` - Search books

3. **Request Format** for mutations:
   ```kotlin
   ActionRequest(
       password: String,
       action: String,  // "add", "update", "delete"
       data: JsonElement  // Book data
   )
   ```

### Error Handling & Result Propagation
**Recent Fix (Critical)**: Previously, all errors in `performAction()` were silently caught with no feedback. This prevented users from knowing if syncs failed in guest mode.

**Current Implementation**:
1. `performAction()` returns `Result<Boolean>`
2. Checks if API is configured (non-empty domain/password)
3. Logs errors with `Log.e()` tag "KtorBookRepository"
4. Repository methods propagate results to use cases
5. Use cases propagate to view models
6. View models display errors in UI

**Error Display**:
- `BookDetailScreen`: Shows success/error toast-like messages
- `AddBookScreen`: Shows error banner at top
- `BookDetailViewModel`: Has fields: `updateInProgress`, `updateSuccess`, `updateError`
- `AddBookViewModel`: Has fields: `isLoading`, `addError`

### Data Flow Example - Update Book
```
BookDetailScreen (user saves)
  → BookDetailViewModel.onEvent(OnSaveClicked)
  → UpdateBookUseCase(updatedBook)
  → KtorBookRepository.updateBook()
  → performAction("update", payload)
  → POST /api/books with ActionRequest
  → Result<Boolean> returned
  → ViewModel updates state (updateSuccess/updateError)
  → UI shows result
```

## Key Files to Know

### High Priority (Frequently Modified)
- `KtorBookRepository.kt`: HTTP implementation, error handling
- `BookDetailViewModel.kt`: Detail screen logic, update handling
- `BookDetailScreen.kt`: Book detail UI, error display
- `SettingsScreen.kt`: API configuration UI
- `BookUiState.kt`: All view model state definitions

### Medium Priority
- `BookRepository.kt`: Interface contract
- `AddBookViewModel.kt`: Add book logic
- `HomeViewModel.kt`: List/search logic
- `AppNavigation.kt`: Screen routing

### Low Priority
- Theme/colors/components
- Mock implementations
- Navigation routes

## Common Tasks & Where to Make Changes

### Add Guest Mode API Feature
1. **API Call**: `KtorBookRepository.kt` - Add new method calling `client.get/post`
2. **Interface**: `BookRepository.kt` - Add method signature
3. **Use Case**: `BookUseCases.kt` - Create new use case class
4. **ViewModel**: Relevant `*ViewModel.kt` - Call use case, handle Result
5. **Screen**: Relevant `*Screen.kt` - Add UI, display errors

### Fix Sync Failures
1. Check `KtorBookRepository.kt` logs: `Log.e(TAG, ...)`
2. Verify API domain/password set in `ThemePreferences`
3. Check `performAction()` for API response status
4. Verify ActionRequest format matches server expectations
5. Check UI error display shows user feedback

### Add New Book Metadata
1. Add field to `Book.kt` domain model
2. Add to `BookDto.kt` mapping
3. Add to `BookDetailUiState.kt` edit fields
4. Add `OutlinedTextField` in `BookDetailScreen.kt`
5. Add `OnEdit<Field>Changed` event in `BookDetailEvent`
6. Handle in `BookDetailViewModel.onEvent()`

### Enable Offline Support
1. Decide on local storage: SQLite Room, DataStore, or file-based
2. Create `LocalBookDataSource` in `data/datasource/`
3. Implement `CacheBookRepository` wrapping `KtorBookRepository`
4. Handle merge conflicts between local and remote
5. Update repository injection in `MainActivity.kt`

## Debugging Tips

### Check API Connectivity
- SettingsScreen has "Test Connection" button
- Makes GET to `/api/public` with limit=1
- Shows "Connected" on success
- Check logcat for Ktor logging (Logging plugin enabled)

### Watch Real Errors (Not Silent)
- Check logcat filter by "KtorBookRepository" tag
- `Log.w()` for warnings (API config incomplete, server errors)
- `Log.e()` for exceptions (detailed stack trace)

### UI State Debugging
- All view models use `MutableStateFlow`
- StateFlow emits on every update
- Can inspect with Android Studio debugger
- Check `uiState.value` in breakpoints

### Network Debugging
- Install Charles Proxy or Wireshark
- Ktor client has logging plugin (LogLevel.INFO)
- Logs all requests/responses to logcat
- Check JSON payload format matches server expectations

## Dependencies
```
// Ktor (HTTP client)
io.ktor:ktor-client-core:2.x
io.ktor:ktor-client-android:2.x
io.ktor:ktor-serialization-kotlinx-json:2.x

// Jetpack Compose
androidx.compose.ui:ui:1.x
androidx.compose.material3:material3:1.x
androidx.lifecycle:lifecycle-viewmodel-compose:2.x

// Image loading
io.coil-kt:coil-compose:2.x

// Kotlin serialization
org.jetbrains.kotlinx:kotlinx-serialization-json:1.x
```

## Testing Checklist
- [ ] Guest mode add/update/delete syncs successfully
- [ ] Error messages display when API credentials invalid
- [ ] API domain/password UI fields are editable
- [ ] Connection test button works
- [ ] Book detail save shows success/error feedback
- [ ] Add book shows errors before submitting
- [ ] Theme toggle saves preference
- [ ] Progress slider updates without saving in edit mode
- [ ] Shelf transitions trigger update

## Future Improvements
1. **Offline Support**: Local database with sync queue
2. **Retry Logic**: Exponential backoff for failed operations
3. **Batch Operations**: Update multiple books at once
4. **Better Stats**: Reading time, books/month metrics
5. **Hilt Dependency Injection**: Replace manual factories
6. **Room Database**: Replace in-memory mock data
7. **FCM Push Notifications**: Remote sync triggers
8. **Image Upload**: Support custom book covers

## Contact/Notes
- **Last Modified**: May 17, 2026
- **Primary Developer Pattern**: MVVM with Compose
- **Architecture Focus**: Testability, clean separation
- **Known Issues**: None (fixed guest mode sync issue)

---
*This document should be updated whenever significant architectural changes are made.*
