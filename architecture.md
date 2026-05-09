# Architecture

User-flow and Clean Architecture / MVVM diagrams for the Crochet Planner
Android app. Keep this file in sync as the navigation graph and module
boundaries evolve.

## User flow

```
                ┌──────────────────────────┐
                │   Splash / Auth Check    │
                └──────────────┬───────────┘
                               │
                ┌──────────────┴───────────┐
                │  Signed in?              │
                └──┬───────────────────┬───┘
                   │ no                │ yes
                   ▼                   ▼
        ┌──────────────────┐   ┌─────────────────────────┐
        │  Sign-in screen  │   │   Home (Pattern         │
        │  (Google / Fire- │   │   History + "Generate") │
        │   base Auth)     │   └────┬────────────────┬───┘
        └────────┬─────────┘        │                │
                 │ success          │ tap saved      │ tap "Generate"
                 └─────────►────────┘ pattern        ▼
                                    │       ┌──────────────────────────┐
                                    ▼       │   New-Pattern Form       │
                          ┌─────────────────┤   • yarn weight (0–7)    │
                          │  Pattern Detail │   • hook size            │
                          │  (read-only)    │   • shape (○ ◯ ▭ □)      │
                          └────────┬────────┘   • dimensions + cm/in   │
                                   │            └────────┬─────────────┘
                                   │                     │ submit
                                   │                     ▼
                                   │         ┌─────────────────────────┐
                                   │         │  Pattern Result         │
                                   │         │  • starting chain count │
                                   │         │  • chain composition    │
                                   │         │  • estimated yarn (yd)  │
                                   │         │  [Save] [New]           │
                                   │         └────────┬────────────────┘
                                   │                  │ save
                                   └──────────────────┘  → back to Home
```

## Clean Architecture / MVVM layers

```
┌────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION (app/)                        │
│  Compose screens ── ViewModels (StateFlow) ── Navigation graph     │
│   AuthScreen   HomeScreen   NewPatternScreen   PatternDetailScreen │
└──────────────────────────────┬─────────────────────────────────────┘
                               │ calls use cases
                               ▼
┌────────────────────────────────────────────────────────────────────┐
│                          DOMAIN (domain/)                          │
│  Pure Kotlin – no Android deps                                     │
│   • Models: Pattern, Shape, YarnSpec, Gauge, ChainSpec, Estimate   │
│   • Use cases: GeneratePattern, EstimateYarn, ListPatterns,        │
│                SavePattern, GetPattern                             │
│   • Pattern generators: Circle / Oval / Rectangle / Square         │
│   • Repositories (interfaces only): PatternRepository, AuthRepo    │
└──────────────────────────────┬─────────────────────────────────────┘
                               │ implemented by
                               ▼
┌────────────────────────────────────────────────────────────────────┐
│                            DATA (data/)                            │
│   Room: PatternEntity, PatternDao, AppDatabase                     │
│   Firebase: AuthDataSource, FirestorePatternDataSource             │
│   Retrofit + OkHttp: scaffolded for future remote APIs             │
│   PatternRepositoryImpl   AuthRepositoryImpl                       │
│   (Hilt provides bindings → domain interfaces)                     │
└────────────────────────────────────────────────────────────────────┘
```

## Notes

- The pattern generator lives entirely in `domain/` with no Android imports
  so it can be unit-tested as plain Kotlin.
- Repositories are **defined** in `domain/` (interfaces) and **implemented**
  in `data/`. Hilt binds the implementations.
- ViewModels expose `StateFlow<UiState>`; screens are stateless and
  recompose from that state.
- Firestore sync of the pattern history is post-MVP; the MVP persists
  locally with Room only.
