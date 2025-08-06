# DoIt - Task Management App

A modern Android task management application built with Kotlin, featuring Firebase authentication, real-time data synchronization, and an intuitive user interface.

<img width="274" height="601" alt="image" src="https://github.com/user-attachments/assets/65a8e3d5-f843-4d94-99bb-5dfe9d3d4021" />
<img width="274" height="601" alt="image" src="https://github.com/user-attachments/assets/b89e5b29-0f5d-420b-9039-12ab4af2c737" />
<img width="274" height="601" alt="image" src="https://github.com/user-attachments/assets/03fb6033-a8bd-4660-8544-f8973ac5fef3" />




## 📱 Features

### 🔐 Authentication
- **User Registration & Login**: Secure email/password authentication using Firebase Auth
- **Password Reset**: Forgot password functionality with email reset
- **Session Management**: Persistent login sessions with local storage
- **Profile Management**: User profile creation and management

### 📋 Task Management
- **Daily Tasks**: Create and manage recurring daily tasks
- **One-Time Tasks**: Handle single-occurrence tasks with specific dates
- **Task Priorities**: Set task priorities (Low, Medium, High)
- **Task Completion**: Mark tasks as complete/incomplete
- **Task Favorites**: Mark important tasks as favorites
- **Task Details**: Add descriptions, dates, and times to tasks

### 🗓️ Calendar Integration
- **Date Selection**: Interactive calendar for task scheduling
- **Date Navigation**: Easy navigation between different dates
- **Task Filtering**: View tasks by specific dates

### 🎨 User Interface
- **Modern Material Design**: Clean and intuitive UI
- **Navigation Drawer**: Easy navigation between different sections
- **Responsive Layout**: Optimized for different screen sizes
- **Splash Screen**: Engaging app launch experience
- **Onboarding**: User-friendly introduction screens

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Authentication, Firestore)
- **UI**: Material Design Components
- **Navigation**: Android Navigation Component
- **Async Operations**: Kotlin Coroutines

### Project Structure
```
app/src/main/java/dev/khaled/doit/
├── data/
│   ├── model/           # Data models (Task, User, TaskPriority)
│   ├── repo/            # Repository implementations
│   └── usecases/        # Business logic use cases
├── di/                  # Dependency injection modules
├── ui/                  # UI components
│   ├── auth/            # Authentication screens
│   ├── home/            # Main app screens
│   ├── onboarding/      # Onboarding flow
│   └── profile/         # Profile management
└── util/                # Utility classes and constants
```

### Key Components

#### Data Models
- **Task**: Core task entity with properties like title, description, date, priority, completion status
- **User**: User information including ID, name, and email
- **TaskPriority**: Enum for task priority levels (Low, Medium, High)

#### Repository Pattern
- **AuthRepo**: Handles user authentication and session management
- **TaskRepo**: Manages task CRUD operations and data persistence

#### Use Cases
- Authentication use cases (Login, SignUp, ForgotPassword)
- Task management use cases (Add, Update, Delete, Complete, Get tasks)

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Google Services account for Firebase

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd doit
   ```

2. **Set up Firebase**
   - Create a new Firebase project
   - Enable Authentication (Email/Password)
   - Enable Firestore Database
   - Download `google-services.json` and place it in the `app/` directory

3. **Build and Run**
   ```bash
   ./gradlew build
   ```
   Or open the project in Android Studio and run it directly.

### Configuration

1. **Firebase Setup**
   - Ensure Firebase project is properly configured
   - Verify `google-services.json` is in the correct location
   - Enable required Firebase services (Auth, Firestore)

2. **Build Configuration**
   - Minimum SDK: 24
   - Target SDK: 33
   - Compile SDK: 34

## 📱 Usage

### First Time Setup
1. Launch the app
2. Complete the onboarding flow
3. Create an account or log in
4. Start adding your tasks!

### Managing Tasks
- **Add Daily Tasks**: Tasks that repeat every day
- **Add One-Time Tasks**: Tasks with specific dates
- **Set Priorities**: Choose Low, Medium, or High priority
- **Mark Complete**: Tap to mark tasks as done
- **View by Date**: Use the calendar to view tasks for specific dates

### Navigation
- **Home**: Overview of daily and one-time tasks
- **Daily Tasks**: Manage recurring daily tasks
- **One Time Tasks**: Handle date-specific tasks
- **Profile**: View and manage your account

## 🔧 Dependencies

### Core Dependencies
- **AndroidX Core**: `1.9.0`
- **Material Design**: `1.12.0`
- **Navigation Component**: `2.5.3`
- **Lifecycle Components**: `2.8.7`
- **Coroutines**: `1.7.3`

### Firebase Dependencies
- **Firebase BOM**: `32.8.1`
- **Firebase Auth**: `22.3.1`
- **Firebase Firestore**: `24.11.1`
- **Firebase Analytics**: `21.6.2`

### Additional Libraries
- **Hilt**: `2.50` (Dependency Injection)
- **Gson**: `2.10` (JSON serialization)
- **Lottie**: `6.3.0` (Animations)
- **Calendar View**: `2.4.1` (Date selection)

## 🧪 Testing

The project includes both unit tests and instrumentation tests:
- **Unit Tests**: Located in `app/src/test/`
- **Instrumentation Tests**: Located in `app/src/androidTest/`

Run tests using:
```bash
./gradlew test          # Unit tests
./gradlew connectedCheck # Instrumentation tests
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**DoIt** - Get things done, one task at a time! ✅
