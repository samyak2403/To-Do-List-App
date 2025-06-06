# ToDo List App - Developer Documentation

This document provides technical details about the architecture, components, and implementation of the ToDo List App.

## Project Structure

The app follows the MVVM (Model-View-ViewModel) architecture pattern and is organized into the following packages:

```
com.samyak.to_dolistapp/
├── adapter/
│   └── TaskAdapter.java           # RecyclerView adapter for displaying tasks
├── data/
│   ├── TaskDao.java               # Data Access Object for Task entity
│   ├── TaskDatabase.java          # Room database configuration
│   └── TaskRepository.java        # Repository pattern implementation
├── model/
│   └── Task.java                  # Task entity class
├── viewmodel/
│   └── TaskViewModel.java         # ViewModel for the UI
├── MainActivity.java              # Main activity for the app
└── SplashActivity.java            # Splash screen activity
```

## Architecture Components

### 1. Model Layer

#### Task Entity (Task.java)

The core data model representing a task with the following attributes:
- `id`: Unique identifier (auto-generated)
- `title`: Task title (required)
- `description`: Task description (optional)
- `completed`: Task completion status (boolean)
- `priority`: Task priority level (1=High, 2=Medium, 3=Low)
- `dueDate`: Due date in milliseconds (optional)
- `createdDate`: Creation timestamp

#### Room Database

Room is used for local data persistence with the following components:

- **TaskDao**: Interface defining database operations:
  - `insert()`: Add a new task
  - `update()`: Update an existing task
  - `delete()`: Delete a task
  - `deleteAllTasks()`: Delete all tasks
  - `deleteCompletedTasks()`: Delete completed tasks
  - `getAllTasks()`: Get all tasks
  - `getActiveTasks()`: Get only active (uncompleted) tasks
  - `getCompletedTasks()`: Get only completed tasks
  - `getTaskById()`: Get a specific task by ID

- **TaskDatabase**: Singleton database instance and configuration

#### Repository

The `TaskRepository` class serves as a clean API for data operations and abstracts the data sources:

- Manages the DAO instance
- Provides methods for all CRUD operations
- Executes database operations on background threads using `ExecutorService`
- Returns `LiveData` objects for observable queries

### 2. ViewModel Layer

The `TaskViewModel` class:
- Extends `AndroidViewModel` to access application context
- Holds UI-related data in a lifecycle-conscious way
- Survives configuration changes
- Provides clean API for the UI layer
- Mediates between the Repository and UI
- Contains no direct references to views or activities

### 3. View Layer

#### Activities

- **SplashActivity**: Entry point with animated splash screen
- **MainActivity**: Main activity with the task list and all interactions

#### Layouts

- **activity_splash.xml**: Splash screen layout
- **activity_main.xml**: Main screen with RecyclerView and FAB
- **item_task.xml**: Individual task item layout
- **dialog_add_edit_task.xml**: Dialog for adding/editing tasks

#### Adapter

`TaskAdapter` extends `ListAdapter` for efficient list updates using `DiffUtil` and includes:
- `TaskViewHolder` inner class for view binding
- Item click listeners
- Animation implementation
- Visual state management

## Key Implementation Details

### Data Flow

1. User interacts with the UI
2. ViewModel processes user actions and updates the Repository
3. Repository manages data operations with the local database
4. Observables (LiveData) notify the UI about data changes
5. UI updates based on the new data

### Task Filtering

Task filtering is implemented through separate queries in the DAO:
- `getAllTasks()`: Returns all tasks
- `getActiveTasks()`: Returns only uncompleted tasks
- `getCompletedTasks()`: Returns only completed tasks

The filter selection is stored in `MainActivity` as `currentFilterMode`.

### Animations

The app implements several animations for a better user experience:
- RecyclerView item animations using `item_animation_fall_down.xml`
- Layout animations using `layout_animation.xml`
- Task completion animations
- FAB animations
- Splash screen animations

### UI/UX Features

- Material Design components
- Custom theme with consistent styling
- Color-coded priority indicators
- Swipe-to-delete with undo functionality
- Empty state handling
- Intuitive task creation and editing

## Testing

### Unit Tests

Unit tests should focus on:
- ViewModel logic
- Repository operations
- DAO queries

### UI Tests

UI tests should verify:
- Task creation workflow
- Task editing functionality
- Filtering behavior
- Swipe-to-delete and undo actions

## Future Improvements

Potential areas for enhancement:
1. Task categories/tags
2. Task search functionality
3. Recurring tasks
4. Reminder notifications
5. Cloud synchronization
6. Dark/light theme support
7. Data import/export
8. Task sharing
9. Advanced sorting options
10. Statistics and productivity insights

## Build and Deployment

### Building the App

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### Dependencies

The app uses the following key dependencies:
- AndroidX Core and AppCompat
- Material Components
- Room for database
- ViewModel and LiveData
- RecyclerView and CardView

See `build.gradle.kts` for the complete list with versions. 
 