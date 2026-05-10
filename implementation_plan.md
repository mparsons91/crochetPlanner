# Implementation Plan

Phased plan for building the Crochet Planner MVP. Phases are roughly
sequential but Phase 1 (pure-Kotlin domain) can be developed in parallel with
Phase 0 once the project compiles.

## Phase 0 — Project bootstrap

1. Open the repo in Android Studio; add Gradle Kotlin DSL config
   (compileSdk current, minSdk 26, Compose BOM).
2. Add dependencies: Compose, Hilt, Room, Coroutines/Flow, Retrofit/OkHttp,
   Firebase BOM (Auth, Firestore), JUnit / Truth / Turbine for tests.
3. Create the three-layer package structure (`app`, `data`, `domain`) — start
   as packages within a single Gradle module; promote to separate modules
   later if build times demand it.
4. Wire Hilt (`@HiltAndroidApp` Application, base activity) and verify a
   no-op build runs on emulator.

## Phase 1 — Domain core (pure Kotlin, fully unit-tested)

5. Define models: `YarnWeight` (enum 0–7), `HookSize`, `Shape` (sealed:
   Circle / Oval / Rectangle / Square), `Dimensions` (with `Unit` enum),
   `Gauge`, `Pattern`, `ChainSpec`, `YarnEstimate`.
6. Build `Gauge.defaultFor(yarnWeight)` from the CYC table in
   `crochet_context.md`.
7. Implement pattern generators in `domain/pattern/`:
   - `CirclePattern`
   - `RectanglePattern`
   - `SquarePattern` (delegates to rectangle with width = height)
   - `OvalPattern`
8. Implement `YarnEstimator` using the per-stitch table from
   `crochet_context.md` §8 plus a 15% margin for tails / weaving in ends.
9. Unit-test each generator: known inputs → expected starting-chain count,
   per-row/round stitch counts, and yarn estimate within tolerance.

## Phase 2 — Persistence + repositories

10. Add Room: `PatternEntity`, `PatternDao` (insert, list newest-first,
    delete, getById), `AppDatabase`, type converters for shape/yarn enums.
11. Implement `PatternRepositoryImpl` (Room-backed for the MVP; Firestore
    sync optional later).
12. Hilt module providing the database, dao, and repository bindings.

## Phase 3 — Auth

13. Configure Firebase project, add `google-services.json`, enable
    Google Sign-In.
14. Implement `AuthDataSource` + `AuthRepositoryImpl` exposing a
    `Flow<AuthState>`.
15. Build `AuthScreen` + `AuthViewModel`; gate the nav graph on auth state.

## Phase 4 — UI

16. Navigation graph: `auth → home → newPattern → patternDetail`.
17. `HomeScreen` + `HomeViewModel`: list past patterns, FAB for "Generate
    new pattern".
18. `NewPatternScreen` + `NewPatternViewModel`: form with cm/in toggle,
    validation, submit calls `GeneratePattern` use case.
19. `PatternDetailScreen` + `PatternDetailViewModel`: render starting chain
    count, chain composition string, yarn-length estimate; save action.
20. Loading / empty / error states for each screen.

## Phase 4.5 — Pattern step tracking

Lets the user check off pattern steps as they crochet, see remaining
stitches at a glance, and resume an in-progress pattern from the home
screen. Slots between Phase 4 (UI shell) and Phase 5 (polish) — without
this, the pattern detail screen is a static document.

### User-visible behaviour

- Each step in `PatternDetailScreen` is tappable. Tapping toggles its
  completion. Tapping a completed step un-completes it (covers the
  fat-finger case). Completed steps render checked + visually
  de-emphasized.
- A **floating progress widget** pinned to the bottom-right of the detail
  view shows the **remaining stitch count** and the **percentage
  complete**. Updates live as steps are toggled.
- When all steps are checked, the widget turns **green** and shows
  **"Complete"**.
- `HomeScreen` pattern cards show the same percentage and the same
  green "Complete" state.

### Domain changes (`domain/repository/SavedPattern.kt`)

- `SavedPattern` gains `completedStepIndices: Set<Int>` (default empty).
- New computed extensions:
  - `completedStitchCount` — sum of `stitchCount` for each completed
    step index.
  - `remainingStitchCount` — `pattern.totalBaseStitches − completedStitchCount`.
  - `percentComplete: Float` in `[0, 1]` — `completedStitchCount /
    pattern.totalBaseStitches`. Foundation steps have `stitchCount = 0`
    and so don't move the percentage on their own.
  - `isComplete: Boolean` — `completedStepIndices.size ==
    pattern.steps.size` (matches the "every step tapped" UX, including
    the foundation).

### Domain repository

- `PatternRepository.updateCompletedSteps(id: String, indices: Set<Int>)`
  — replaces the set atomically. Simpler than per-step add/remove and
  easier to reason about under concurrent taps.

### Persistence

- `PatternEntity` gains:
  - `completedStepIndicesJson: String` (JSON array of ints, default `"[]"`).
  - `completedStitchCount: Int` and `stepCount: Int` denormalized so
    the home list can render progress without deserializing the full
    Pattern blob.
- `HooklineDatabase` version bumps `1 → 2`. MVP uses
  `fallbackToDestructiveMigration()`; a real Room migration can ship
  later once users have valuable history.
- `PatternDao` gains `suspend fun updateProgress(id, json,
  completedStitchCount)`.

### UI

- `PatternDetailScreen`:
  - Each step becomes a clickable `Row` with a Checkbox (or icon) +
    instruction text. Whole card area toggles state.
  - Floating progress widget: a `Box` overlay using
    `Modifier.align(Alignment.BottomEnd)`. Shows `"{remaining} sts
    left"` on one line and `"{percent}% complete"` on another. Container
    colour switches to a green palette and label to `"Complete"` when
    `isComplete`.
- `HomeScreen` `PatternListItem`:
  - Adds a `LinearProgressIndicator` and percentage label below the
    existing metadata row.
  - When `isComplete`, the percentage text is replaced by a green
    "Complete" pill.

### Tests

- Domain unit tests for the new computed extensions: empty set → 0%
  and not complete; all step indices → 100% and complete; foundation-
  only marked → 0% but not complete; partial completion produces the
  correct stitch count and percentage.
- Mapper round-trip: `SavedPattern.completedStepIndices` survives
  entity → JSON → entity.
- ViewModel test for `PatternDetailViewModel.toggleStep` is a stretch
  (Hilt + Flow + StateFlow plumbing); the simpler unit-tests on the
  computed properties cover the core logic.

### Open design choices to confirm before implementation

1. **Percentage basis** — currently stitch-count based, per the spec
   wording ("based on the total stitches required"). Trade-off:
   tapping the foundation gives a checkmark but doesn't move the
   percentage. Alternative: step-count based, which is more responsive
   but doesn't match the spec wording.
2. **"Complete" definition** — currently "every step tapped" (so the
   foundation must be tapped too). Alternative: `percent == 100%`,
   which would let users skip the foundation tap and still see green.
3. **Migration** — destructive on the `v1 → v2` bump for the MVP.
   Acceptable since users today only have patterns they generated
   themselves and can regenerate.

## Phase 5 — Polish + handoff

21. Manual test on device/emulator: full sign-in → generate → save →
    revisit flow for each shape.
22. Add Crashlytics + basic analytics events (pattern generated by shape).
23. Write a `README.md` quickstart (Firebase setup, run instructions).

## Stretch goals (post-MVP, in priority order)

- **Pattern visualization** — Canvas rendering using the chart symbols in
  `crochet_context.md` §9. Confirm symbol mappings against an authoritative
  source before starting.
- **User-overridable gauge** from a swatch the user measures.
- **Multi-color** patterns and color-change notation.
- **Complex stitch patterns** beyond plain sc/hdc/dc rounds and rows.
- **Granny-square** style square; additional shapes (triangle, hexagon, hat).
- **Firestore sync** for cross-device history.
- **UK terminology** toggle.
