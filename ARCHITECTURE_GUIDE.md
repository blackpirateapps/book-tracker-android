# Book Tracker App - Complete Architecture Guide

## Project Structure Overview

```
book-tracker-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/booktracker/app/
│       │   ├── BookTrackerApp.kt           (Application entry point)
│       │   ├── MainActivity.kt              (Activity with Compose setup)
│       │   ├── domain/                      (Pure Kotlin - business logic)
│       │   │   ├── model/
│       │   │   │   ├── Book.kt
│       │   │   │   └── SearchBook.kt
│       │   │   ├── repository/
│       │   │   │   └── BookRepository.kt   (Interface)
│       │   │   └── usecase/
│       │   │       └── BookUseCases.kt     (All use cases)
│       │   ├── data/                       (Android + Ktor implementations)
│       │   │   ├── preferences/
│       │   │   │   └── ThemePreferences.kt
│       │   │   ├── repository/
│       │   │   │   ├── KtorBookRepository.kt
│       │   │   │   └── MockBookRepository.kt
│       │   │   ├── remote/dto/
│       │   │   │   ├── BookDto.kt
│       │   │   │   └── SearchBookDto.kt
│       │   │   └── datasource/
│       │   │       └── MockBookDataSource.kt
│       │   └── presentation/               (Compose UI - MVVM)
│       │       ├── navigation/
│       │       │   ├── AppNavigation.kt
│       │       │   └── Screen.kt
│       │       ├── screens/
│       │       │   ├── HomeScreen.kt
│       │       │   ├── BookDetailScreen.kt
│       │       │   ├── AddBookScreen.kt
│       │       │   └── SettingsScreen.kt
│       │       ├── viewmodel/
│       │       │   ├── BookUiState.kt
│       │       │   ├── HomeViewModel.kt
│       │       │   ├── BookDetailViewModel.kt
│       │       │   └── AddBookViewModel.kt
│       │       ├── components/
│       │       │   ├── BookCard.kt
│       │       │   └── AppBottomNavigation.kt
│       │       ├── theme/
│       │       │   ├── Color.kt
│       │       │   ├── Shape.kt
│       │       │   └── Type.kt
│       │       └── refresh/
│       │           └── AppRefreshBus.kt
│       └── res/
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── API_DOCS.md
```

## Architecture Layers

### Layer 1: Domain (Pure Kotlin)
**Location**: `domain/`
**Dependencies**: None (Kotlin stdlib only)
**Purpose**: Business logic, interfaces, entities

#### Models
- **Book**: Internal domain model with full metadata
  - id, title, author(s), shelf, progress, dates, etc.
  - Immutable data class
  
- **SearchBook**: Lightweight model for search results
  - Just enough info to show search results
  - Maps from OpenLibrary API

- **ShelfType**: Enum with 4 values
  - READING_LIST (to-read)
  - READING (currently reading)
  - READ (finished)
  - ABANDONED (gave up)
  - Each has `displayName` and `apiValue`

#### Repository Interface
```kotlin
interface BookRepository {
    // Reads - return data or empty
    suspend fun getBooks(): List<Book>
    suspend fun getBookById(id: String): Book?
    
    // Writes - return Result for error handling
    suspend fun addBook(book: Book): Result<Boolean>
    suspend fun updateBook(book: Book): Result<Boolean>
    suspend fun deleteBook(id: String): Result<Boolean>
    
    // Connection/API methods
    suspend fun testConnection(): Result<Boolean>
    suspend fun searchBooks(query: String): Result<List<SearchBook>>
}
```

#### Use Cases
Individual operator classes wrapping repository calls:
- Each takes repository in constructor
- Implements `invoke()` operator for compact syntax: `useCase(params)`
- Returns what repository returns

### Layer 2: Data (Android/Ktor)
**Location**: `data/`
**Dependencies**: Domain, Ktor, Android

#### Implementations
- **KtorBookRepository**: Live HTTP implementation
  - Uses Ktor HTTP client
  - Reads API credentials from ThemePreferences
  - All actual API communication happens here
  - Error logging with "KtorBookRepository" tag
  
- **MockBookRepository**: In-memory for testing/development
  - No network calls
  - Returns test data
  - Useful for UI development without API

#### Data Models (DTOs)
- **BookDto**: JSON deserializable version of Book
  - Handles API field names (camelCase vs snake_case)
  - Handles missing fields gracefully
  - `toDomain()` method converts to Book model
  
- **SearchBookDto**: OpenLibrary API response mapping
  - Fields: key, title, author_name, first_publish_year, cover_i
  - Converts to SearchBook domain model

#### Preferences
- **ThemePreferences**: SharedPreferences wrapper
  - `apiDomain: String` - Guest mode server URL
  - `apiPassword: String` - Guest mode auth
  - `isDarkModeEnabled: Boolean` - Theme preference
  - All changes persist immediately

### Layer 3: Presentation (Compose/MVVM)
**Location**: `presentation/`
**Dependencies**: Domain, Data, Jetpack Compose, Lifecycle

#### MVVM Pattern
**ViewModel** → **StateFlow** → **Composable Screen**

Each screen has:
1. **State Data Class**: Immutable state
   - All UI state in one object
   - `copy()` for updates
   - No mutable state exposed

2. **Event Sealed Class**: User actions
   - One event per user interaction
   - ViewModels receive events, update state

3. **ViewModel**: State management
   - Holds MutableStateFlow
   - Exposes StateFlow as read-only
   - Implements onEvent(event) handler
   - Calls use cases in viewModelScope

#### Screens
- **HomeScreen**: Main list/search view
  - Shows books in grid or list
  - Filters by shelf
  - Search functionality
  - FAB to add book

- **BookDetailScreen**: Full book information
  - Shows all metadata
  - Edit mode for updating
  - Shelf management
  - Progress tracking

- **AddBookScreen**: Manual book entry
  - Title/author input
  - Shelf selection
  - Progress slider
  - Form validation

- **SettingsScreen**: Configuration & preferences
  - API domain/password input (editable TextFields)
  - Connection test button
  - Dark mode toggle
  - About info

#### Navigation
- **AppNavigation**: Composable function containing NavHost
  - Defines all routes
  - Creates and manages ViewModels
  - Handles navigation events
  - Injectable repository & use cases

- **Screen**: Enum with route helpers
  ```kotlin
  enum class Screen {
      Home, BookDetail, Settings, History;
      val route = this.name // "BookDetail"
  }
  ```

#### Refresh Bus
- **AppRefreshBus**: Global event bus
  - Allows screens to force refresh
  - Uses Flow for reactivity
  - Decouples screens from each other

## Guest Mode Data Flow

### Configuration
```
SettingsScreen (UI)
  ↓
OutlinedTextField (apiDomain)
  ↓
onApiDomainChanged callback
  ↓
ThemePreferences.apiDomain = value
  ↓
Stored in SharedPreferences
  ↓ (on app restart)
ThemePreferences (retrieved)
  ↓
KtorBookRepository.baseUrl (property getter)
```

### Fetching Books
```
HomeScreen (UI)
  ↓
HomeViewModel.loadBooks()
  ↓
GetBooksUseCase()
  ↓
KtorBookRepository.getBooks()
  ↓
GET $baseUrl/api/books
  ↓
Parse response to List<BookDto>
  ↓
Map to List<Book>
  ↓
Return to HomeViewModel
  ↓
Update MutableStateFlow
  ↓
Recompose HomeScreen with new list
```

### Updating Book (With Error Handling)
```
BookDetailScreen (user saves)
  ↓
BookDetailEvent.OnSaveClicked
  ↓
BookDetailViewModel.onEvent(OnSaveClicked)
  ↓
ViewModel sets updateInProgress = true
  ↓
UpdateBookUseCase(updatedBook) [suspend]
  ↓
KtorBookRepository.updateBook(book): Result<Boolean>
  ↓
performAction("update", payload): Result<Boolean>
  ├─ Validate apiDomain not empty
  ├─ Validate apiPassword not empty
  ├─ POST to $baseUrl/api/books
  ├─ ActionRequest { password, "update", data }
  ├─ Check response.status.isSuccess()
  └─ Return Result
  ↓
ViewModel receives Result
  ├─ If Success:
  │   └─ updateSuccess = "Book saved"
  │   └─ updateInProgress = false
  └─ If Failure:
      └─ updateError = exception.message
      └─ updateInProgress = false
  ↓
UI recomposes
  └─ Shows success/error container
```

## Key Architectural Decisions

### Why Result<T> for Write Operations?
- Can't throw exceptions across suspend boundaries
- Result captures both success and failure
- Allows UI to show meaningful error messages
- Consistent with Kotlin conventions

### Why StateFlow?
- Reactive: Screens automatically recompose on state change
- Testable: Can collect values in tests
- Thread-safe: Safe to update from any coroutine context
- Lifecycle-aware: Compose handles collection lifecycle

### Why View Models with Manual Factory?
- Hilt not set up yet (comment in code indicates future setup)
- Manual factories needed to pass dependencies
- Factory pattern lets each ViewModel define its requirements

### Why Refresh Bus as Global Event?
- Decouples screens from each other
- HomeScreen can trigger reload without knowing about detail screen
- Detail screen updates don't need to manually refresh list

### Why Separate Ktor Client in Repository?
- Single long-lived HttpClient instance
- Connection pooling and resource reuse
- Centralized logging configuration
- Easy to mock for testing

## Error Handling Strategy

### Three Levels of Error Handling

1. **Repository Level** (KtorBookRepository)
   - Validates inputs
   - Makes requests
   - Returns Result with exception details
   - Logs with proper levels (Error, Warning, Info)

2. **Use Case Level** (BookUseCases)
   - Propagates Result as-is
   - No transformation needed
   - Just passes through to ViewModel

3. **ViewModel Level** (NavigationViewModel)
   - Receives Result
   - Updates state with error message
   - UI reads state and displays message

### Logging Pattern
```kotlin
Log.i(TAG, "Successful operation")      // Info: normal flow
Log.w(TAG, "Config incomplete warning")  // Warning: config issues
Log.e(TAG, "Network error", exception)   // Error: with exception
```

## Common Development Scenarios

### Scenario: Add New API Endpoint
1. Add method to `BookRepository` interface
2. Implement in `KtorBookRepository` using Ktor client
3. Implement mock in `MockBookRepository`
4. Create `UseCase` wrapper if needed
5. Call from appropriate ViewModel
6. Display results in Composable

### Scenario: Add New Book Field
1. Add to `Book` domain model
2. Add to `BookDto` with proper JSON mapping
3. Add to `BookDetailUiState` edit fields
4. Add UI input in `BookDetailScreen`
5. Add event type in `BookDetailEvent`
6. Handle in `BookDetailViewModel.onEvent()`
7. Include in update payload in `KtorBookRepository.updateBook()`

### Scenario: Debug Sync Failure
1. Check logcat: `adb logcat KtorBookRepository:E`
2. Look for "API configuration incomplete" (credentials not set)
3. Look for server status errors (400, 401, 500, etc.)
4. Look for network errors (timeout, host unreachable)
5. Verify UI shows error message to user
6. Check ActionRequest payload format matches API spec

### Scenario: Add Offline Support
1. Create `LocalBookDataSource` using Room/SQLite
2. Create `CacheBookRepository` wrapping `KtorBookRepository`
3. On getBooks(): check local first, sync from remote if stale
4. On updateBook(): update local immediately, queue remote update
5. Create sync service to process queue when online
6. Inject `CacheBookRepository` instead of `KtorBookRepository`

## Testing Approach

### Unit Tests (Would test)
- ViewModels: Mock use cases, verify state changes
- Use cases: Mock repository, verify calls
- Models: Serialization/deserialization

### Integration Tests (Would test)
- Repository: Mock Ktor responses, verify parsing
- DataStore: Verify preference persistence

### Manual Testing (Currently used)
- Test Connection button in SettingsScreen
- Add/edit/delete books and verify sync
- Watch logcat for error messages
- Try invalid credentials and incomplete config

## Performance Considerations

1. **HttpClient Reuse**: Single instance in KtorBookRepository, not created per request
2. **Coroutines**: All repository calls suspend, don't block UI thread
3. **StateFlow**: Only subscribers collect updates, not all state
4. **Image Loading**: Using Coil for efficient image caching
5. **List Rendering**: LazyColumn/LazyVerticalGrid only compose visible items

## Security Considerations

1. **Password Storage**: Stored in SharedPreferences (unencrypted by default)
   - Should upgrade to EncryptedSharedPreferences in production
   
2. **API Credentials**: In memory while app running
   - Cleared on app close
   - No credentials in logs
   
3. **HTTPS**: API domain can be HTTP or HTTPS
   - Should validate server certificates
   
4. **Data**: Book data could contain sensitive content
   - No SQLite encryption currently

## Dependencies (build.gradle.kts)
```kotlin
// Core Android
androidx.core:core-ktx:1.x
androidx.lifecycle:lifecycle-runtime-ktx:2.x

// Compose
androidx.activity:activity-compose:1.x
androidx.compose.ui:ui:1.x
androidx.compose.material3:material3:1.x
androidx.lifecycle:lifecycle-compose:2.x

// Navigation
androidx.navigation:navigation-compose:2.x

// Ktor HTTP Client
io.ktor:ktor-client-android:2.x
io.ktor:ktor-client-core:2.x
io.ktor:ktor-serialization-kotlinx-json:2.x

// JSON Serialization
org.jetbrains.kotlinx:kotlinx-serialization-json:1.x

// Image Loading
io.coil-kt:coil-compose:2.x

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core:1.x
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.x
```

---
*This guide should be updated whenever architectural changes are made.*
*Last updated: May 17, 2026*
