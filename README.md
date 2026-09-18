# Android Production Template & CI/CD Engine

A modern, production-grade Android application template built with **Jetpack Compose**, **Material 3**, **Robolectric**, **Roborazzi**, and a complete **GitHub Actions CI/CD Pipeline** with automated Keystore generation and signing.

Designed specifically to serve as a high-reliability starter template for **AI Agents** (e.g. Gemini, Claude, Antigravity, Cursor) and human developers building enterprise-quality Android apps.

---

## 📑 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Repository Structure](#-repository-structure)
- [CI/CD Automation Matrix](#-cicd-automation-matrix)
- [Android Release Keystore Guide](#-android-release-keystore-guide)
  - [Automated Keystore via GitHub Actions](#1-automated-keystore-via-github-actions)
  - [Local Keystore Generation Scripts](#2-local-keystore-generation-scripts)
  - [Manual Keystore Creation](#3-manual-keystore-creation-with-keytool)
  - [Configuring GitHub Repository Secrets](#4-configuring-github-repository-secrets)
- [AI Agent Customization Playbook](#-ai-agent-customization-playbook)
  - [1. App Identity & Namespace Sync](#1-app-identity--namespace-sync)
  - [2. Dependency Management](#2-dependency-management)
  - [3. Secrets & API Keys](#3-secrets--api-keys)
  - [4. Testing Strategy & Screenshot Recording](#4-testing-strategy--screenshot-recording)
  - [5. TestTag Semantics for Automated QA](#5-testtag-semantics-for-automated-qa)
- [Local Development & Gradle Tasks](#-local-development--gradle-tasks)
- [License & Contribution](#-license)

---

## 🏛 Architecture Overview

- **UI Framework:** Jetpack Compose (Material 3) with dynamic color theming, edge-to-edge system insets, and dark/light modes.
- **Language & Runtime:** Kotlin 2.x, Java 21, Android SDK 36 (target), minSdk 24.
- **State Management:** MVVM / MVI architecture using `ViewModel`, Kotlin Coroutines `StateFlow`, and `collectAsStateWithLifecycle`.
- **Testing Engine:**
  - **JVM Unit Tests:** JUnit 4 & AndroidX Test.
  - **Robolectric:** Fast, headless JVM-based Android framework tests (no emulator required).
  - **Roborazzi:** Visual regression and automated screenshot verification.
- **Build System:** Gradle (Kotlin DSL `.gradle.kts`) with Version Catalog (`gradle/libs.versions.toml`).
- **Secrets Management:** Secrets Gradle Plugin reading from `.env` and `.env.example`, generating compile-time type-safe `BuildConfig` variables.

---

## 📂 Repository Structure

```
.
├── .github/
│   └── workflows/
│       ├── ci.yml                    # Automated PR/Push validation (Lint, Tests, Roborazzi, Debug APK)
│       ├── release.yml               # Automated production release (Keystore decoding, Signed APK & AAB)
│       └── generate-keystore.yml     # On-demand workflow to create release keystores inside GitHub
├── app/
│   ├── build.gradle.kts              # Application build config, signing configs, and dependencies
│   ├── proguard-rules.pro            # Code shrinking & obfuscation configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   # App declarations and permissions
│       │   ├── java/com/example/     # Kotlin Compose application source code
│       │   └── res/                  # Vector drawables, strings, colors, adaptive launcher icon
│       └── test/                     # Unit, Robolectric, and Roborazzi screenshot tests
├── gradle/
│   └── libs.versions.toml            # Centralized Gradle Version Catalog
├── scripts/
│   ├── generate-keystore.sh          # Linux / macOS shell script for keystore creation
│   └── generate-keystore.ps1         # Windows PowerShell script for keystore creation
├── .env.example                      # Template environment variables (safe to commit)
├── metadata.json                     # AI Studio platform identification and capabilities
└── settings.gradle.kts               # Gradle settings & plugin repositories
```

---

## 🚀 CI/CD Automation Matrix

This template comes with 3 fully automated, battle-tested GitHub Actions workflows:

### 1. Continuous Integration (`.github/workflows/ci.yml`)
Runs on every push to `main`/`master`/`develop` and on every pull request:
- **Environment Setup:** Configures Java 17 Temurin, initializes Gradle caching via `gradle/actions/setup-gradle@v4`.
- **Fallback Secrets:** Copies `.env.example` to `.env` if not present so Secrets Gradle Plugin never fails in CI.
- **Linting:** Runs Android Lint (`./gradlew lintDebug`).
- **JVM Tests:** Executes all unit and Robolectric tests (`./gradlew testDebugUnitTest`).
- **Visual Regression:** Executes Roborazzi screenshot verification (`./gradlew verifyRoborazziDebug`).
- **Artifacts:** Automatically uploads test reports, failure diff images, and the assembled `debug.apk`.

### 2. Release & Signing (`.github/workflows/release.yml`)
Runs automatically on Git tags (e.g. `v1.0.0`) or via manual trigger (`workflow_dispatch`):
- **Keystore Decoding:** Safely decodes the `KEYSTORE_BASE64` secret into `my-upload-key.jks`.
- **Zero-Failure Fallback:** If `KEYSTORE_BASE64` has not yet been configured in the repository, the workflow generates a temporary release keystore on-the-fly to validate the build without throwing unrecoverable errors.
- **Signed Artifacts:** Assembles signed Release APK (`assembleRelease`) and signed Google Play App Bundle (`bundleRelease`).
- **GitHub Release:** Publishes a release with the signed APK and AAB attached as downloadable assets.

### 3. Keystore Generator (`.github/workflows/generate-keystore.yml`)
Manual workflow (`workflow_dispatch`) to generate a cryptographically strong, Google Play-compatible release keystore directly inside GitHub Actions:
- Uses Java `keytool` with RSA 2048-bit keys and 30+ year validity.
- Masks the output passwords and writes step-by-step secret addition guides directly to the GitHub Action summary.

---

## 🔑 Android Release Keystore Guide

Google Play requires all production apps and updates to be digitally signed by an **Upload Keystore** (`.jks` or `.keystore`).

### 1. Automated Keystore via GitHub Actions
If you are already running this repository on GitHub:
1. Go to the **Actions** tab in your repository.
2. Select **Generate Release Keystore** in the left sidebar.
3. Click **Run workflow**.
4. Once completed, download the artifact containing your keystore, and review the Job Summary for instructions.

### 2. Local Keystore Generation Scripts

#### Linux / macOS:
```bash
chmod +x ./scripts/generate-keystore.sh
./scripts/generate-keystore.sh
```

#### Windows PowerShell:
```powershell
.\scripts\generate-keystore.ps1
```

Both scripts generate:
- `my-upload-key.jks` (the binary keystore file)
- `my-upload-key.jks.base64.txt` (the Base64-encoded string for GitHub Secrets)

### 3. Manual Keystore Creation with `keytool`
You can also generate a keystore manually using JDK's `keytool`:

```bash
keytool -genkeypair \
  -v \
  -keystore my-upload-key.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "YourStrongPassword123!" \
  -keypass "YourStrongPassword123!" \
  -dname "CN=Android Developer, OU=Mobile, O=My Company, L=San Francisco, ST=CA, C=US"
```

To encode it for GitHub Actions:
- **Linux:** `base64 -w 0 my-upload-key.jks > keystore.base64.txt`
- **macOS:** `base64 -i my-upload-key.jks -o keystore.base64.txt`
- **Windows (PowerShell):** `[Convert]::ToBase64String([IO.File]::ReadAllBytes('my-upload-key.jks')) | Out-File -Encoding ascii keystore.base64.txt`

### 4. Configuring GitHub Repository Secrets
Navigate to your GitHub repository:
**Settings** > **Secrets and variables** > **Actions** > **New repository secret**

Add the following 4 secrets:

| Secret Name | Description | Example Value |
|---|---|---|
| `KEYSTORE_BASE64` | Entire text content of `keystore.base64.txt` | `MIIDvTCCAqW...` |
| `STORE_PASSWORD` | Password of the keystore | `YourStrongPassword123!` |
| `KEY_PASSWORD` | Password of the key alias | `YourStrongPassword123!` |
| `KEY_ALIAS` | Key alias name | `upload` |
| `ENV_FILE` *(Optional)* | Contents of production `.env` | `GEMINI_API_KEY=...` |

---

## 🤖 AI Agent Customization Playbook

When an AI Agent is tasked with turning this template into a feature-complete application, it **MUST** follow these systematic steps:

### 1. App Identity & Namespace Sync
1. **Set `applicationId`:** In `app/build.gradle.kts`, set `applicationId = "com.aistudio.<app_theme>.<random>"`.
   - ⚠️ **DO NOT** change `namespace = "com.example"` — this preserves the source folder structure and resource bindings.
2. **Synchronize App Name across 3 files:**
   - In `app/src/main/res/values/strings.xml`: `<string name="app_name">Your App Name</string>`
   - In `settings.gradle.kts`: `rootProject.name = "Your App Name"`
   - In `metadata.json`: `"name": "Your App Name"`
3. **Synchronize Unit Tests:** Update `app/src/test/java/com/example/ExampleRobolectricTest.kt` to expect the updated string:
   `assertEquals("Your App Name", appName)`.

### 2. Dependency Management
This template includes pre-configured, tested dependencies in `gradle/libs.versions.toml`:
- **Uncommenting Dependencies:** To enable features like Room, Retrofit, Firebase Auth, or CameraX, uncomment the pre-configured dependencies in `app/build.gradle.kts`.
- **Kebab-case to Dot-notation:** When referencing a Version Catalog dependency from `libs.versions.toml` in `app/build.gradle.kts`:
  - `androidx-navigation-compose` ➡️ `libs.androidx.navigation.compose`
  - `converter-moshi` ➡️ `libs.converter.moshi`
- **APK Optimization:** Keep unused dependencies commented out to minimize build times and APK footprint.

### 3. Secrets & API Keys
- Never hardcode API keys or credentials in Kotlin source files.
- Place placeholder keys in `.env.example`:
  ```env
  GEMINI_API_KEY=MY_GEMINI_API_KEY
  ```
- Access the injected keys via `BuildConfig` in your Kotlin code:
  ```kotlin
  val apiKey = BuildConfig.GEMINI_API_KEY
  ```

### 4. Testing Strategy & Screenshot Recording
- **Running Tests:** Always run `./gradlew testDebugUnitTest` to verify business logic and Robolectric CUJs.
- **Recording Screenshots:** When UI changes are made, update reference screenshots with:
  ```bash
  gradle :app:recordRoborazziDebug
  ```
- **Verifying Screenshots:** Confirm no unintended visual drift occurs with:
  ```bash
  gradle :app:verifyRoborazziDebug
  ```

### 5. TestTag Semantics for Automated QA
To make the application testable by AI agents and automated testing frameworks:
- Add `Modifier.testTag("descriptive_snake_case_id")` to all primary buttons, input fields, cards, and toggles.
- Ensure all interactive elements have a minimum touch target size of `48.dp`.

---

## 🛠 Local Development & Gradle Tasks

| Command | Action |
|---|---|
| `gradle :app:assembleDebug` | Build debug APK |
| `gradle :app:testDebugUnitTest` | Run JUnit & Robolectric unit tests |
| `gradle :app:verifyRoborazziDebug` | Verify UI against Roborazzi golden screenshots |
| `gradle :app:recordRoborazziDebug` | Generate/update golden screenshot baselines |
| `gradle :app:lintDebug` | Run Android Lint static analysis |
| `gradle :app:assembleRelease` | Build signed Release APK |
| `gradle :app:bundleRelease` | Build signed Release Google Play App Bundle (AAB) |

---

## 📄 License

This template is distributed under the Apache 2.0 License. Designed for AI studio builders, developers, and autonomous coding agents worldwide.
