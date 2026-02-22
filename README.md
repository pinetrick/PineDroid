# PineDroid

[![](https://jitpack.io/v/pinetrick/PineDroid.svg)](https://jitpack.io/#pinetrick/PineDroid)

A powerful Android development library providing database ORM, debugging tools, and utility components for rapid development.

## ✨ Features

### 🗄️ Database ORM
- **ActiveRecord-style ORM**: Fluent, chainable query API
- **Auto Schema Management**: Automatically creates and manages table structure based on data classes
- **Type Safe**: Fully leverages Kotlin's type system
- **Relationship Queries**: Support for custom relationship methods (one-to-many, one-to-one, etc.)

### 🐛 Debug Tools
- **Floating Debug Window**: Runtime debugging information display
- **HTTP Request Monitor**: Track and inspect HTTP request queue
- **Task Manager**: Monitor background task execution status
- **Database Browser**: Visual database viewing and editing

### 📸 Image Handling
- **Image Picker**: Gallery selection and camera capture
- **CameraX Integration**: Modern camera API
- **Image Preview**: Built-in image preview functionality
- **EXIF Information**: Extract image metadata

### 📍 Location Services
- **GPS Positioning**: High-precision location acquisition
- **Route Recording**: Track recording functionality
- **Google Maps Integration**: Maps Compose support
- **Foreground Service**: Continuous location tracking

### 🎨 UI Components
- **Jetpack Compose**: Built entirely with Compose
- **Material Design 3**: Follows latest design specifications
- **Custom Components**: Buttons, image loading, animations, etc.
- **Dark Mode Support**

### 🔧 Other Utilities
- **File Manager**: Built-in file browsing and editing
- **Text Editor**: Code-level text editing functionality
- **Step Counter**: Activity Recognition integration
- **Firebase Integration**: Auth, Analytics, Crashlytics

## 📦 Installation

### Step 1: Add JitPack Repository

Add to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add Dependency

Add to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.pinetrick:PineDroid:0.0.18")
}
```

### Step 3: Sync Project

Click "Sync Now" to sync Gradle configuration.

## 🚀 Quick Start

### 1. Database ORM Usage

#### Define Data Model

```kotlin
import com.pine.pinedroid.db.bean.BaseDataTable

data class User(
    override var id: Long? = null,
    var username: String,
    var password: String,
    var email: String? = null,
    var createTime: Date = Date()
) : BaseDataTable() {
    // Define relationship: one user has many articles
    fun articles() = model<Article>().where("user_id", id).select()

    // Define relationship: one user has one profile
    fun profile() = model<UserProfile>().find(profileId)
}
```

#### Create Tables

```kotlin
fun initDatabase() {
    // Auto-create tables based on data classes
    table<User>().createTable()
    table<Article>().createTable()
    table<UserProfile>().createTable()
}
```

#### CRUD Operations

```kotlin
// Create record
val user = User(
    username = "Pine",
    password = "password123",
    email = "pine@example.com"
).save()

// Find single record
val user = model<User>().where("username", "Pine").find()

// Query multiple records
val users = model<User>()
    .where("age", ">", 18)
    .order("create_time DESC")
    .limit(10)
    .select()

// Complex queries: AND and OR combination
val users = model<User>()
    .where("age", ">", 18)
    .whereOr("vip_level", ">", 5)
    .select()

// Update record
user.password = "newPassword456"
user.save()

// Delete record
user.delete()

// Count records
val count = model<User>().where("age", ">", 18).count()

// Raw SQL query
val results = model<User>().rawQuery(
    "SELECT * FROM users WHERE age > ?",
    arrayOf(18)
)
```

#### Relationship Queries

```kotlin
// Load associated data
val user = model<User>().where("username", "Pine").find()!!
val articles = user.articles() // Get all user's articles
val profile = user.profile()   // Get user profile

articles.forEach { article ->
    println("Article: ${article.title}")
}
```

### 2. Enable Debug Tools

#### Initialize in Application

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable debug mode
        if (BuildConfig.DEBUG) {
            PineConfig.setIsDebug(true)
        }
    }
}
```

#### Show Floating Debug Window

The debug window will automatically display on startup (requires overlay permission). Access through the window:
- Database Browser
- SQL Executor
- HTTP Request Monitor
- Task Manager
- File Explorer

### 3. Using Image Picker

```kotlin
// Launch image picker
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val selectedImages = result.data?.getParcelableArrayListExtra<Uri>("selected_images")
        // Handle selected images
    }
}

Button(onClick = {
    val intent = Intent(context, ImagePickupActivity::class.java).apply {
        putExtra("max_selection", 9) // Max 9 images
        putExtra("enable_camera", true) // Allow camera
    }
    launcher.launch(intent)
}) {
    Text("Select Images")
}
```

### 4. Location Services

```kotlin
// Start location tracking
PineLocationService.startTracking(context) { location ->
    val latitude = location.latitude
    val longitude = location.longitude
    // Handle location updates
}

// Stop tracking
PineLocationService.stopTracking(context)
```

### 5. HTTP Requests (Ktor-based)

```kotlin
import com.pine.pinedroid.net.Http

// GET request
val response = Http.get("https://api.example.com/users")

// POST request
val response = Http.post("https://api.example.com/users") {
    setBody(mapOf(
        "username" to "Pine",
        "email" to "pine@example.com"
    ))
}
```

## ⚙️ Configuration

### Required Permissions

The library automatically declares the following permissions in manifest, but runtime permissions need to be requested in your app:

```xml
<!-- Basic permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Debug window (optional) -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Camera and storage (if using image features) -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Location services (if using location features) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### ProGuard Rules

If code obfuscation is enabled, the library already includes necessary ProGuard rules. Data model classes are automatically preserved, no additional configuration needed.

### Firebase Configuration (Optional)

If using Firebase-related features, add `google-services.json` to your project.

## 📋 Requirements

- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 36
- **Kotlin**: 2.2.20+
- **Jetpack Compose**: BOM 2025.08.00+

## 📱 Demo App

After cloning the project, run the `:app` module to see a complete feature demonstration:

```bash
git clone https://github.com/pinetrick/PineDroid.git
cd PineDroid
./gradlew :app:installDebug
```

## 🏗️ Architecture

PineDroid adopts modern Android development architecture:

- **MVVM Pattern**: ViewModel + State + Composable UI
- **Jetpack Compose**: Fully declarative UI
- **Kotlin Coroutines**: Asynchronous processing
- **Ktor Client**: Network requests
- **SQLite**: Local database

For detailed architecture documentation, see [CLAUDE.md](./CLAUDE.md).

## 📝 Documentation

- [CLAUDE.md](./CLAUDE.md) - Complete development documentation and architecture guide
- [API Documentation](https://jitpack.io/com/github/pinetrick/PineDroid/latest/javadoc/) - Auto-generated API docs

## 🤝 Contributing

Issues and Pull Requests are welcome!

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## 🔗 Links

- [GitHub Repository](https://github.com/pinetrick/PineDroid)
- [JitPack](https://jitpack.io/#pinetrick/PineDroid)
- [Issue Tracker](https://github.com/pinetrick/PineDroid/issues)

## 📮 Contact

For questions or suggestions:

- Submit a [GitHub Issue](https://github.com/pinetrick/PineDroid/issues)
- Email: admin@intbr.com

---

**Made with ❤️ by Pine**
