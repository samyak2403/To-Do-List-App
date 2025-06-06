# ToDo List App

A modern, feature-rich to-do list application for Android that helps you organize your tasks efficiently with a beautiful user interface.

## Features

- **Task Management**: Create, edit, and delete tasks with ease
- **Priority Levels**: Assign High, Medium, or Low priority to your tasks
- **Due Dates**: Set due dates for your tasks to stay on schedule
- **Task Filtering**: Filter tasks by All, Active, or Completed status
- **Visual Indicators**: Color-coded priority indicators for better visual organization
- **Swipe to Delete**: Quickly remove tasks with a simple swipe gesture
- **Undo Functionality**: Easily restore accidentally deleted tasks
- **Modern UI/UX**: Beautiful Material Design interface with smooth animations

## Screenshots

![Splash Screen](screenshots/splash_screen.png)
![Task List](screenshots/task_list.png)
![Add Task](screenshots/add_task.png)

## Installation

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- Android SDK with minimum API level 24 (Android 7.0 Nougat)
- Java Development Kit (JDK) 11

### Steps to Run the App

1. Clone the repository:
   ```
   git clone https://github.com/samyak2403/ToDoListApp.git
   ```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned repository and click "Open"

3. Wait for Gradle to sync the project

4. Connect an Android device or use the emulator

5. Click the "Run" button (green triangle) in Android Studio

## Usage Guide

### Adding a New Task

1. Open the app
2. Tap the "+" floating action button at the bottom right
3. Fill in the task details:
   - **Title**: Enter a name for your task (required)
   - **Description**: Add more details about the task (optional)
   - **Priority**: Select High, Medium, or Low priority
   - **Due Date**: Set a deadline for your task (optional)
4. Tap "Save" to create the task

### Editing a Task

1. Tap on any task in the list
2. Modify any details as needed
3. Tap "Save" to update the task

### Completing a Task

1. Check the checkbox next to the task title
2. The task will be marked as completed with a strikethrough text

### Filtering Tasks

1. Tap the filter icon in the top right
2. Select from:
   - **All Tasks**: Shows all tasks
   - **Active Tasks**: Shows only uncompleted tasks
   - **Completed Tasks**: Shows only completed tasks

### Deleting Tasks

- **Individual Task**: Swipe left or right on a task to delete it
- **Completed Tasks**: Use the menu option "Delete Completed Tasks"
- **All Tasks**: Use the menu option "Delete All Tasks"

### Restoring a Deleted Task

After swiping to delete, tap the "UNDO" button in the snackbar that appears at the bottom of the screen to restore the task.

## Technical Implementation

### Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern with the following components:

- **Model**: Room Database with Task entity
- **View**: Activities and layouts
- **ViewModel**: Handles UI-related data and business logic

### Key Components

- **Room Database**: For local data persistence
- **LiveData**: For observable data that respects the app's lifecycle
- **ViewModel**: To manage UI-related data in a lifecycle-conscious way
- **RecyclerView**: For efficient display of the task list
- **Material Components**: For modern, consistent UI elements

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design for the UI/UX guidelines
- Android Jetpack libraries for the architecture components 
