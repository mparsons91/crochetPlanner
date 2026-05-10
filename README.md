# Hookline

An Android crochet pattern planner. Pick a yarn weight, hook size, and a
shape with dimensions; the app generates a beginner-friendly pattern with
the starting chain count, step-by-step instructions, and an estimated
yarn length, then lets you check off steps as you crochet.

Working repo name is `crochetPlanner`; the published app/launcher name is
**Hookline**.

## Status

MVP feature-complete. Authenticated single-color patterns for the four
shapes below, persistent local history, and step-by-step progress
tracking.

| MVP requirement | Status |
|---|---|
| Google sign-in (Firebase Auth) | ✅ |
| Home with pattern history + "New pattern" FAB | ✅ |
| Form: yarn weight, hook size, shape, dimensions, in/cm toggle | ✅ |
| Shapes: circle, oval, rectangle, square | ✅ |
| Output: starting chain count + composition | ✅ |
| Output: estimated yarn length | ✅ |
| Tap-to-complete steps + progress widget + home progress | ✅ |
| Pattern visualization | ❌ stretch |

Stretch goals tracked in [implementation_plan.md](implementation_plan.md).

## Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin 2.2.21 |
| UI | Jetpack Compose (BOM 2026.02.01) + Material 3 |
| Architecture | MVVM + Clean Architecture (`app` / `data` / `domain`) |
| DI | Hilt 2.57 + KSP |
| Local DB | Room 2.7.2 |
| Async | Coroutines + Flow |
| Networking | Retrofit 2 + OkHttp 4 + kotlinx-serialization |
| Backend | Firebase Auth (Google sign-in via Credential Manager) |
| Build | Gradle 8.10.2, AGP 8.7.3, Kotlin DSL |

Toolchain rationale (we're on AGP 8.7 rather than 9.x because of
plugin-ecosystem timing) is documented in [CLAUDE.md](CLAUDE.md).

## Prerequisites

- **Android Studio** Ladybug or newer. The bundled JDK is fine; you do
  not need a separately installed JDK.
- **Android SDK** API 35 (compile) / API 26 minimum. Android Studio
  installs these on first run.
- **A Firebase project** — see [Firebase setup](#firebase-setup) below.
  Without `app/google-services.json`, Gradle sync will fail on the
  google-services plugin.
- **An Android phone or emulator** with Google Play services for Google
  sign-in to work.

## First-time setup

```bash
git clone https://github.com/mparsons91/crochetPlanner.git
cd crochetPlanner
```

Then in Android Studio: **File → Open** the cloned folder. Let Gradle
sync. The first sync downloads everything and takes 5–15 minutes.

### Firebase setup

You need your own Firebase project for sign-in to work. Each developer
machine needs its own `google-services.json` (it is gitignored).

1. Create a Firebase project at
   [console.firebase.google.com](https://console.firebase.google.com).
2. Add an Android app:
   - **Package name:** `com.matthewparsons.hookline`
   - **Debug SHA-1 fingerprint:** run `./gradlew :app:signingReport` and
     copy the `SHA1` line for variant `debug`.
3. Download the generated **`google-services.json`** and place it at
   **`app/google-services.json`**.
4. In the Firebase console: **Build → Authentication → Get started →
   Sign-in method → Google → enable**, set a support email, save.

Re-sync Gradle in Android Studio. The google-services Gradle plugin
processes `google-services.json` and generates a string resource
(`R.string.default_web_client_id`) that the auth screen uses with
Credential Manager.

### Run on a device

- Plug in an Android phone with **USB debugging** enabled and **Allow
  USB debugging** when prompted, OR create an emulator via **Tools →
  Device Manager**.
- Press the green ▶ Run button. First build is slow; subsequent builds
  are 10–30s.

## Project layout

```
app/src/main/java/com/matthewparsons/hookline/
├── HooklineApplication.kt        @HiltAndroidApp entry point
├── MainActivity.kt               @AndroidEntryPoint host
├── domain/                       Pure Kotlin: no Android deps
│   ├── auth/                       AuthUser, AuthState, AuthRepository
│   ├── model/                      YarnWeight, HookSize, Shape, Pattern, …
│   ├── pattern/                    PatternEngine + per-shape generators
│   └── repository/                 PatternRepository, SavedPattern + progress
├── data/                         Implementations of domain interfaces
│   ├── auth/                       FirebaseAuthRepository
│   ├── local/                      Room entity, DAO, database, mappers
│   └── repository/                 PatternRepositoryImpl
├── di/                           Hilt modules (Database, Auth, Repository)
└── ui/                           Compose screens + ViewModels
    ├── HooklineApp.kt              Top-level composable + auth gate + NavHost
    ├── auth/                       Sign-in screen
    ├── home/                       Pattern history + FAB
    ├── newpattern/                 Generation form
    ├── detail/                     Pattern detail + step tracking
    └── navigation/                 Route constants
```

## Building and testing

```bash
# debug APK
./gradlew :app:assembleDebug

# unit tests (43 tests, ~5s)
./gradlew :app:testDebugUnitTest

# install on connected device
./gradlew :app:installDebug

# get debug keystore SHA-1 (for Firebase setup)
./gradlew :app:signingReport
```

Test reports land at `app/build/reports/tests/testDebugUnitTest/`.

## Documentation

- [CLAUDE.md](CLAUDE.md) — orientation for Claude Code sessions; project
  conventions and toolchain decisions.
- [crochet_context.md](crochet_context.md) — domain reference: stitches,
  yarn weights, hook sizes, pattern notation, shape recipes, yarn-length
  estimation.
- [architecture.md](architecture.md) — ASCII user-flow + Clean
  Architecture / MVVM layer diagram.
- [implementation_plan.md](implementation_plan.md) — phased build plan
  (Phases 0–5 plus stretch goals); design rationale for each phase.

## License

TBD.
