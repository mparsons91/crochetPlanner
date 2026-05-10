# CLAUDE.md

This file gives Claude Code the context it needs to be useful in this repo.
Keep it short and current; deeper domain notes live in dedicated files.

## What this app is

**Hookline** (working repo name: `crochetPlanner`) – an Android app that
generates beginner-friendly crochet patterns for simple shapes from user
inputs (yarn weight, hook size, shape, dimensions). The headline output for
the MVP is:

1. The number of links in the **starting chain**.
2. The composition of that initial chain (and follow-on rounds/rows).
3. An **estimated length of yarn** required to complete the shape.

Visualization of the worked piece is a **stretch goal**, not part of the MVP.

## Key documents

- `README.md` – contributor-facing quickstart: prerequisites, Firebase setup,
  build/run/test commands, project layout. Update when those mechanics
  change.
- `crochet_context.md` – distilled crochet domain knowledge: stitches, yarn
  weights, hook size chart, pattern notation, shape recipes, and yarn-length
  estimation formulas. Read this before touching anything in the pattern
  generator.
- `architecture.md` – ASCII user-flow diagram + Clean Architecture / MVVM
  layer diagram. Update when the navigation graph or module boundaries
  change.
- `implementation_plan.md` – phased build plan (Phases 0–5 + stretch goals).
  Update as phases complete or scope shifts.

## MVP scope

Supported shapes: **circle, oval, rectangle, square**. Single color only.
Inputs: yarn weight (CYC 0–7), hook size, shape, dimensions, unit (cm/in).

User-visible flow:

1. Sign in with Google (Firebase Auth).
2. Home: list of past patterns + "Generate new pattern" button.
3. New-pattern form: yarn size, hook size, shape, dimensions (cm/in toggle).
4. Result screen: starting chain count, chain composition, estimated yarn
   length. Save to history.

## Tech stack (target)

| Layer        | Choice                                |
|--------------|---------------------------------------|
| Language     | Kotlin                                |
| UI           | Jetpack Compose                       |
| Architecture | MVVM + Clean Architecture (data / domain / presentation) |
| DI           | Hilt                                  |
| Local DB     | Room                                  |
| Async        | Coroutines + Flow                     |
| Networking   | Retrofit + OkHttp                     |
| Backend      | Firebase (Auth + Firestore)           |
| Build        | Gradle (Kotlin DSL), Android Studio   |

The pattern generator itself is **pure Kotlin in the `domain` layer** – no
Android dependencies, fully unit-testable.

## Module / package layout (planned)

```
app/                       – Compose UI, ViewModels, navigation, Hilt entry points
data/                      – Room DAOs, Firebase repositories, Retrofit services
domain/                    – Pure Kotlin: pattern models, generator, use cases
```

The pattern generator lives in `domain/pattern/` with one file per shape
(`CirclePattern.kt`, `OvalPattern.kt`, etc.) plus a shared `Gauge` /
`YarnEstimator` utility.

## Conventions and decisions (so far)

- US crochet terminology in storage and computation (UK is a future toggle).
- Yarn-length output is always presented as an **estimate**; include a margin
  (~10–15%) for tails and weaving in ends.
- Default gauge comes from the CYC standard for the chosen yarn weight; the
  user can override later (post-MVP).
- The square shape is implemented as a width = height rectangle for the MVP;
  granny-square style is a stretch goal.

## Toolchain (pinned in `gradle/libs.versions.toml`)

- **AGP 8.7.3** with Gradle 8.10.2. The Android Studio wizard generated AGP
  9.2.1 + Gradle 9.4.1, but as of writing the Hilt / KSP / kotlin-android
  plugin ecosystem doesn't yet support AGP 9 (`BaseExtension` was removed).
  Revisit AGP 9 when those plugins catch up.
- **Kotlin 2.2.21** (matched KSP `2.2.21-2.0.5`).
- **Java/JVM target 17** for both `compileOptions` and Kotlin's
  `compilerOptions`.
- **Hilt 2.57** with KSP (not kapt).
- **compileSdk / targetSdk 35**, **minSdk 26**.
- `android.useAndroidX=true` is required in `gradle.properties` (AGP 8 default
  is off).
- Firebase BOM + plugin are deferred to Phase 3 (they need
  `google-services.json` to sync without errors).

## How to work in this repo

- Project is currently a fresh Android Studio repo (initial commit only).
  Most of the implementation work below has not been done yet.
- When adding files, follow the module layout above. New shapes go in
  `domain/pattern/` and need both a generator and a unit test.
- Always update `crochet_context.md` when discovering domain facts that
  changed an implementation decision.
- Keep this file lean: prefer linking to dedicated docs over inlining detail.

## Open items

- Confirm crochet chart symbol mappings against an authoritative source
  before any chart rendering work (the crochet.com source was inaccessible
  during initial research).
- Calibrate per-stitch yarn-length constants against real swatches before
  promoting the estimator beyond "best-effort."
- Decide on a single oval starting-chain convention and document it inline
  with the oval generator.
