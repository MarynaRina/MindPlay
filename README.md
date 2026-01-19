# MindPlay 🧠

A modern Android application designed to train memory, concentration, and mental agility through engaging cognitive games. Built with Jetpack Compose and Kotlin, MindPlay offers a stress-free approach to brain training with smooth animations, immersive sound design, and an intuitive user interface.

## 📱 Overview

MindPlay is a collection of six cognitive training games that help users improve various mental skills:
- **Memory** - Pattern matching and visual memory
- **Simon** - Sequential memory and pattern reproduction
- **Arithmetic** - Mental math and numerical reasoning
- **Pary (Pairs)** - Logical matching and pattern recognition
- **Puzzle** - Spatial reasoning and problem-solving
- **Uwaga (Attention)** - Reaction time and focus

The app emphasizes a relaxed, pressure-free experience while still providing challenging gameplay that adapts to user preferences.

## 🚀 Features

### Core Features
- **Six Cognitive Games** - Each targeting different mental skills
- **Smooth Animations** - Fade transitions between screens using `AnimatedVisibility`
- **Immersive Audio** - Background music and sound effects for user interactions
- **Custom Theme System** - Consistent design with MindPlay theme colors
- **Progress Tracking** - Game statistics and performance metrics
- **Daily Reminders** - Push notifications to encourage regular practice
- **Onboarding Flow** - Welcome and tutorial screens for first-time users
- **Settings Management** - Customizable preferences stored locally

### Game Features
- Multiple difficulty levels and grid modes
- Optional stress/time modes for added challenge
- Pause functionality during gameplay
- Detailed result screens with metrics
- Play again or return to menu options
- Intro screens explaining game rules

## 🛠️ Technologies

### Core Stack
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern declarative UI framework
- **Material 3** - Design system and components
- **Coroutines & Flow** - Asynchronous programming and reactive streams

### Architecture & Dependencies
- **MVVM Architecture** - Clean separation of concerns
- **Koin** - Dependency injection (v3.5.3)
  - `koin-android`
  - `koin-androidx-compose`
- **Navigation Compose** - Type-safe navigation (v2.7.6)
- **DataStore** - Preferences storage (v1.0.0)
- **Room Database** - Local data persistence (v2.6.1)
  - Runtime, KTX extensions, and Kapt compiler
- **Lifecycle & ViewModel** - Android Architecture Components

### Build Configuration
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 36
- **Compile SDK:** 36
- **Java Version:** 11
- **Gradle:** Kotlin DSL

## 📂 Project Structure

```
app/src/main/java/com/mind/play/
├── MainActivity.kt                 # Entry point with lifecycle management
├── MindPlayApp.kt                  # Application class with Koin setup
├── core/
│   ├── components/                 # Reusable UI components
│   │   ├── AnimatedCard.kt
│   │   ├── GameResultScreen.kt
│   │   ├── MindPlayBottomNavigation.kt
│   │   ├── MindPlayProgressBar.kt
│   │   ├── MindPlayToggle.kt
│   │   └── Buttons (Primary, Secondary)
│   ├── di/
│   │   └── AppModule.kt           # Dependency injection modules
│   ├── navigation/
│   │   ├── NavGraph.kt            # Navigation setup and routes
│   │   ├── NavigationAnimations.kt
│   │   └── Screen.kt              # Screen destinations
│   ├── notifications/
│   │   ├── NotificationScheduler.kt
│   │   ├── ReminderReceiver.kt
│   │   └── BootReceiver.kt
│   └── sound/
│       └── SoundManager.kt        # Audio playback management
├── data/
│   ├── local/                     # Room database entities and DAOs
│   └── repository/                # Data layer abstractions
├── domain/
│   └── models/                    # Business logic models
├── ui/
│   ├── dashboard/
│   │   └── HomeScreen.kt          # Main dashboard
│   ├── games/
│   │   ├── GamesScreen.kt         # Games selection grid
│   │   ├── arithmetic/            # Simple arithmetic game
│   │   ├── memory/                # Card matching game
│   │   ├── pary/                  # Logical pairs game
│   │   ├── puzzle/                # Sliding puzzle game
│   │   ├── simon/                 # Color sequence game
│   │   └── uwaga/                 # Attention/reaction game
│   ├── onboarding/
│   │   ├── WelcomeScreen.kt
│   │   └── OnboardingScreen.kt
│   ├── settings/
│   │   └── SettingsScreen.kt
│   ├── splash/
│   │   └── SplashScreen.kt
│   └── theme/
│       └── Theme files             # Colors, typography, shapes
└── res/
    ├── drawable/                   # Icons and graphics
    ├── values/                     # Strings, colors, themes
    └── raw/                        # Audio files
```

### Architecture Pattern

The app follows **MVVM (Model-View-ViewModel)** architecture:

```
View (Composables) ← ViewModel ← Repository ← Data Source
                        ↓
                   StateFlow/State
```

Each game module contains:
- **Screen.kt** - Main composable with game logic
- **ViewModel.kt** - State management and business logic
- **Models.kt** - Game state and data classes
- **IntroScreen.kt** - Game rules and configuration
- **components/** - Game-specific UI components

## 🎮 Screens & Navigation Flow

### Navigation Graph

```
Splash Screen
    ↓
    ├─→ Welcome Screen (first launch)
    │       ↓
    │   Onboarding Screen
    │       ↓
    └─→ Home Screen ←→ Bottom Navigation
            ↓              ↓              ↓
        Dashboard      Games Menu     Settings
                           ↓
                    [Individual Games]
                    ├─ Arithmetic
                    ├─ Memory
                    ├─ Pary
                    ├─ Puzzle
                    ├─ Simon
                    └─ Uwaga
                           ↓
                    [Game Flow]
                    Intro → Gameplay → Results
```

### Screen Descriptions

#### Core Screens
- **SplashScreen** - Initial loading screen with logo
- **WelcomeScreen** - First-time user greeting
- **OnboardingScreen** - Interactive tutorial walkthrough
- **HomeScreen** - Main dashboard with quick access
- **GamesScreen** - Grid view of all available games
- **SettingsScreen** - User preferences and app configuration

#### Game Screens
Each game follows a consistent flow:
1. **Intro Screen** - Game rules, difficulty selection
2. **Game Screen** - Active gameplay with timer, progress, pause
3. **Result Screen** - Performance metrics, play again option

### Game Mechanics

**Arithmetic (Prosta arytmetyka)**
- Solve addition and subtraction problems
- Multiple choice answers
- Progress tracking with score display
- Optional time limit mode

**Memory**
- Match pairs of cards by flipping them
- Multiple grid sizes (2x4, 3x4, 4x4)
- Round-based progression
- Time-based challenges

**Simon**
- Watch and memorize color sequences
- Reproduce the pattern by tapping colors
- Sequences grow longer each round
- Audio-visual feedback

**Pary (Pairs)**
- Match logical pairs (concepts, opposites, etc.)
- Grid-based selection
- Correct/incorrect visual feedback
- Multiple rounds with increasing difficulty

**Puzzle**
- Sliding tile puzzle (3x3 or 4x4 grid)
- Move tiles to complete the image
- Move counter and timer
- Win/lose conditions

**Uwaga (Attention)**
- React to specific stimuli
- Tap correct targets as they appear
- Reaction time tracking
- Configurable difficulty

## 🔊 Sound System

The app features a comprehensive audio system via `SoundManager`:
- **Background Music** - Continuous ambient music during gameplay
- **Sound Effects:**
  - Tap/click sounds for button interactions
  - Success sounds for correct answers
  - Error sounds for mistakes
  - Card flip sounds
  - Completion fanfare

Audio playback is managed through the Activity lifecycle:
- Automatically pauses when app goes to background
- Resumes when app returns to foreground
- Properly released on app termination

## 💾 Data Persistence

### DataStore (Preferences)
- User settings (sound, notifications)
- Onboarding completion status
- Theme preferences

### Room Database
- Game progress and statistics
- User performance history
- Achievement tracking

## 🎨 Theming

MindPlay uses a custom theme system with:
- **Primary Colors** - Purple/violet palette
- **Secondary Colors** - Complementary accents
- **Semantic Colors** - Success (green), error (red), warning
- **Custom Typography** - Rubik font family
- **Material 3 Components** - Modern design language
- **Dark/Light Mode Support** - Adaptive themes

Theme colors are accessed via `MindPlayTheme.colors.*`

## 🔔 Notifications

The app includes a notification system for daily reminders:
- **ReminderReceiver** - Handles scheduled notifications
- **BootReceiver** - Restores alarms after device restart
- **NotificationScheduler** - Manages notification timing
- Permissions: `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`

## 🧪 Testing

### Test Structure
- **Unit Tests** - JUnit (implementation included)
- **Instrumented Tests** - AndroidX Test + Espresso
- **Compose Tests** - UI testing with `ui-test-junit4`

Test packages:
```
app/src/androidTest/java/    # Instrumented tests
app/src/test/java/           # Unit tests
```

### Running Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test
./gradlew test --tests "com.mind.play.ExampleTest"
```

## 📦 Installation & Setup

### Prerequisites
- **Android Studio** - Hedgehog (2023.1.1) or later
- **JDK** - Version 11 or higher
- **Android SDK** - API 26+ (minimum), API 36 (target)
- **Gradle** - 8.0+ (included via wrapper)

### Clone & Build

```bash
# Clone the repository
git clone <repository-url>
cd MindPlay

# Grant execute permission to Gradle wrapper (macOS/Linux)
chmod +x gradlew

# Build the project
./gradlew build

# Install on connected device/emulator
./gradlew installDebug
```

### Configuration Files

The project uses these key configuration files:
- `build.gradle.kts` (project & app level) - Build configuration
- `gradle.properties` - Gradle settings
- `settings.gradle.kts` - Project structure
- `gradle/libs.versions.toml` - Dependency version catalog
- `local.properties` - SDK location (generated, not in VCS)
- `proguard-rules.pro` - Code obfuscation rules

### First Run

1. Launch Android Studio
2. Open the project directory
3. Wait for Gradle sync to complete
4. Select a device/emulator (API 26+)
5. Click Run ▶️

The app will display the splash screen, then the welcome/onboarding flow for first-time users.

## 🎯 Key Implementation Details

### Navigation Animations
All screen transitions use fade animations:
```kotlin
enterTransition = { NavigationAnimations.fadeInTransition() }
exitTransition = { NavigationAnimations.fadeOutTransition() }
```

### Game State Management
Games use Kotlin StateFlow for reactive UI updates:
```kotlin
val gameState by viewModel.gameState.collectAsState()
```

### Dependency Injection
Koin modules provide singleton instances:
```kotlin
single { SoundManager(androidContext()) }
single { SettingsRepository(get()) }
```

### Composable Patterns
- **AnimatedVisibility** - Smooth transitions between game phases
- **LazyVerticalGrid** - Efficient grid layouts for game cards
- **remember/mutableStateOf** - Local state management
- **collectAsState** - Flow to Compose state conversion


