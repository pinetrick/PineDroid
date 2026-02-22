# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PineDroid is an Android library that provides debugging tools, utilities, and a custom database ORM for Android applications. The project consists of two modules:

- `:PineDroid` - The library module (namespace: `com.pine.pinedroid`)
- `:app` - Demo/test application showcasing library features (namespace: `com.pine.pindroidpp`)

## Common Development Commands

### Building and Running

```bash
# Build the entire project
./gradlew build

# Build only the library module
./gradlew :PineDroid:build

# Build only the app module
./gradlew :app:build

# Install and run the demo app on a connected device/emulator
./gradlew :app:installDebug

# Build release version with ProGuard enabled
./gradlew :PineDroid:assembleRelease
```

### Testing

```bash
# Run unit tests for the library
./gradlew :PineDroid:test

# Run unit tests for the app
./gradlew :app:test

# Run instrumented tests on a connected device
./gradlew :PineDroid:connectedAndroidTest
./gradlew :app:connectedAndroidTest
```

### Cleaning

```bash
# Clean all build artifacts
./gradlew clean

# Clean specific module
./gradlew :PineDroid:clean
```

## Architecture Overview

### MVVM Pattern with Jetpack Compose

The project follows a strict MVVM architecture pattern. Each screen/feature is organized with three components:

1. **ViewModel** (`*VM.kt`) - Extends `BaseViewModel<TState>`, handles business logic
2. **State** (`*State.kt`) - Data class holding UI state
3. **Screen** (`*Screen.kt`) - Composable UI that observes state

Example structure:
```
feature/
  ├── FeatureScreen.kt      # Composable UI
  ├── FeatureScreenState.kt # UI state data class
  └── FeatureScreenVM.kt    # ViewModel extending BaseViewModel<FeatureScreenState>
```

### Database ORM System

PineDroid includes a custom ActiveRecord-style ORM located in `com.pine.pinedroid.db`:

#### Key Components:

- **`BaseDataTable`** - Abstract base class for all data models. Provides `save()`, `delete()`, `createOrUpdate()` methods
- **`Model`** - Query builder for database operations. Created via `model<T>()` or `model(name, dbName)`
- **`Table`** - Schema management for creating/altering tables. Created via `table<T>()` or `table(name, dbName)`
- **`DbRecord`** - Raw database record wrapper with key-value access
- **`DbConnection`** - Database connection manager

#### ORM Usage Patterns:

```kotlin
// Data models extend BaseDataTable
data class User(
    override var id: Long? = null,
    var username: String,
    var password: String
) : BaseDataTable() {
    // Define relationships as methods
    fun articles() = model<Article>().where("user_id", id).select()
}

// Creating/updating records
User(username = "Pine", password = "pwd").save()

// Querying with chainable methods
val user = model<User>().where("username", "Pine").find()
val users = model<User>().where("age", ">", 18).order("created_at").limit(10).select()

// Relationships are manually defined as methods returning queries
```

**Note:** Relationships are NOT automatic - define them as methods in your data class that return appropriate queries.

### Debug Tools Architecture

Located in `com.pine.pinedroid.debug`:

- **Floating Debug Window** (`debug/window/`) - System overlay window for runtime debugging
- **HTTP Queue Monitor** (`debug/http_queue/`) - Track and inspect HTTP requests
- **Task Manager** (`debug/task_manager/`) - View and manage background tasks

Debug mode is controlled via `PineConfig.setIsDebug(boolean)` or automatically enabled in debug builds.

### Key Utilities

- **`PineApp`** - Application context utilities (`com.pine.pinedroid.utils.PineApp`)
- **Extension Functions** - Extensive Kotlin extensions in `utils/*Ext.kt` files for common operations
- **Logging** - `com.pine.pinedroid.utils.log` package with `loge()`, `logd()`, etc.
- **HTTP Client** - Ktor-based client in `com.pine.pinedroid.net.Http`
- **Image Handling** - `com.pine.pinedroid.file.image` package with image pickup and processing utilities

## Module Dependencies

The library (`:PineDroid`) includes:
- Jetpack Compose (full Material3 support)
- Ktor client (OkHttp and CIO engines)
- Kotlin Coroutines
- Firebase (Analytics, Auth, Crashlytics)
- Google Play Services (Maps, Location, Review)
- CameraX for camera features
- Accompanist (Permissions, SystemUI, Pager)

The demo app (`:app`) depends on the `:PineDroid` module and demonstrates library usage.

## Build Configuration

- **compileSdk**: 36
- **minSdk**: 26 (library), 28 (app)
- **Java Version**: 11
- **Kotlin Version**: 2.2.20
- **Android Gradle Plugin**: 8.13.0
- **Compose**: Enabled with BOM 2025.08.00

## Code Organization

```
PineDroid/src/main/java/com/pine/pinedroid/
├── activity/          # Built-in utility activities (file explorer, image picker, SQL runner, etc.)
├── db/                # Database ORM system
├── debug/             # Debug tools (floating window, HTTP monitor, task manager)
├── file/              # File and image handling utilities
├── firebase/          # Firebase integration utilities
├── google/            # Google services (Maps, etc.)
├── hardware/          # Hardware utilities (GPS, sensors, camera)
├── jetpack/           # Jetpack Compose utilities and components
│   ├── animation/
│   ├── ui/
│   └── viewmodel/     # BaseViewModel and navigation helpers
├── language/          # Localization utilities
├── net/               # Networking (HTTP client, image cache)
├── screen/            # Reusable screen components
├── system/            # System utilities
├── ui/                # UI components and themes
└── utils/             # General utilities and extensions
```

## Navigation

The project uses Jetpack Compose Navigation. ViewModels can trigger navigation via `BaseViewModel`:

- Extend `BaseViewModel<TState>`
- Use navigation events via `NavEvent` sealed class
- Handle navigation in Composables with `HandleNavigation(viewModel)`

## ProGuard Configuration

Release builds have minification enabled. Keep rules are defined in:
- `PineDroid/proguard-rules.pro`
- `PineDroid/consumer-rules.pro`

Classes annotated with `@Keep` are preserved during shrinking.

## Important Notes

- **Database Tables**: Tables are auto-created on first access if missing. Table structure is inferred from data class properties (excluding fields starting with `_` or `$`)
- **Field Naming**: Class property names (camelCase) are automatically converted to snake_case for database column names
- **Transient Fields**: Use `@Transient` annotation to exclude fields from database operations
- **Debug Window**: Requires `SYSTEM_ALERT_WINDOW` permission to display floating window
- **Firebase**: Configured but requires `google-services.json` to be added by consuming apps
- **ProGuard**: The library applies ProGuard rules to release builds - test release builds thoroughly
