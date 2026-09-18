# AI Agent Developer Guide: Android Template

This guide provides machine-executable instructions and architectural constraints for **AI Coding Agents** modifying or extending this codebase.

---

## 🎯 Primary Directives for AI Agents

1. **Namespace vs ApplicationId**:
   - `namespace` in `app/build.gradle.kts` is strictly bound to `com.example`. **NEVER** change this string unless explicitly instructed to perform a total codebase directory refactoring.
   - `applicationId` in `app/build.gradle.kts` **MUST** be unique for every distinct app project (`com.aistudio.<feature>.<random>`).

2. **Platform & Resource Name Triple-Sync**:
   Whenever the app's identity or title is modified, the following files **MUST** be updated in unison:
   - `metadata.json`: `"name": "New Name"`
   - `app/src/main/res/values/strings.xml`: `<string name="app_name">New Name</string>`
   - `settings.gradle.kts`: `rootProject.name = "New Name"`
   - `app/src/test/java/com/example/ExampleRobolectricTest.kt`: verify string assertion matches `"New Name"`.

3. **CI/CD Safety & Keystore Fallbacks**:
   - Never remove or break `.github/workflows/ci.yml` or `.github/workflows/release.yml`.
   - `app/build.gradle.kts` is configured with an automatic fallback in `signingConfigs.release`: if `KEYSTORE_PATH` or `STORE_PASSWORD` are not present, it safely signs with `debugConfig`. This prevents build failures in containerized agent environments.

4. **Secrets Management via BuildConfig**:
   - **Rule**: DO NOT hardcode API credentials.
   - Declare any required environment variable in `.env.example`.
   - The `secrets-gradle-plugin` injects these into `BuildConfig.<KEY_NAME>`.
   - Check for empty values gracefully at runtime before making network calls.

---

## 📋 Standard Extension Recipes

### Recipe A: Adding Room Database Persistence
1. In `app/build.gradle.kts`, ensure Room dependencies and KSP compiler are enabled:
   ```kotlin
   implementation(libs.androidx.room.runtime)
   implementation(libs.androidx.room.ktx)
   ksp(libs.androidx.room.compiler)
   ```
2. Create Entity (`@Entity`), DAO (`@Dao`), and Database (`@Database(entities = [...], version = 1)`).
3. Inject the Room database instance into your `ViewModel` or Repository.

### Recipe B: Adding Type-Safe Jetpack Compose Navigation
1. In `app/build.gradle.kts`, uncomment:
   ```kotlin
   implementation(libs.androidx.navigation.compose)
   ```
2. Define navigation routes as `@Serializable` Kotlin objects or data classes:
   ```kotlin
   @Serializable object HomeRoute
   @Serializable data class DetailRoute(val itemId: String)
   ```
3. Use `NavHost(navController, startDestination = HomeRoute)` with `composable<HomeRoute> { ... }`.

### Recipe C: Updating UI & Roborazzi Screenshot Tests
Whenever modifying composables covered by screenshot tests (e.g. `GreetingScreenshotTest.kt`):
1. Make sure your Compose UI uses `Modifier.testTag(...)` on interactive components.
2. If the visual appearance changed intentionally:
   - Run `gradle :app:recordRoborazziDebug` to update reference golden screenshots in `src/test/screenshots/`.
   - Verify with `gradle :app:verifyRoborazziDebug`.

---

## ⚠️ Anti-Patterns (STRICTLY FORBIDDEN)
- ❌ Do NOT create or edit `local.properties`. The build environment uses `ANDROID_SDK_ROOT`.
- ❌ Do NOT run `gradle clean` unless strictly required for resolving corrupt cache issues.
- ❌ Do NOT introduce instrumented tests in `androidTest/` that require real devices or running ADB emulators. Always use Robolectric for local JVM execution.
- ❌ Do NOT commit `.jks`, `.keystore`, `.env`, or credential tokens to Git.
