# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew clean                  # Clean build artifacts
./gradlew test                   # Run unit tests (JVM)
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lint                   # Run lint checks
```

## Project Overview

Perplexy is an Android educational quiz/puzzle game with three game modes: Logic (type 0), Riddles (type 1), and Sequences (type 2). The app features a coin economy, character unlocking, in-app purchases, rewarded/interstitial ads, and social sharing.

- **App ID**: `com.contextgenesis.perplexy`
- **Multi-module**: `app` + `unity-ads` (local AAR module)
- **Min SDK**: 16, **Target SDK**: 25

## Architecture

### Key Activities
- `MainActivity` — Entry point; fullscreen immersive ViewPager with 3 tabs (Settings | Front Page | Statistics)
- `QuestionsActivity` — Core gameplay; implements `QuestionsCallback` interface for fragment-to-activity communication
- `NumberLineActivity` — Specialized number line visualization for certain question types
- `CharacterStore` — Avatar/character unlock shop

### Fragment-Based UI
`QuestionsActivity` hosts one of three question fragments depending on question type:
- `QuestionWordFragment` — word/letter answer input
- `QuestionTextBoxFragment` — free text input
- `QuestionMCQFragment` — multiple choice

`MainActivity` uses `FragmentStatePagerAdapter` for its 3 swipeable tabs.

### Data Layer
- **Questions** are loaded from JSON assets (`sequences.json`, `riddles.json`, `logic.json`) via `JSONUtils` using LoganSquare annotations on `GenericQuestion`
- **Progress** is persisted via Sugar ORM into `sugar_orm.db`; `GenericAnswerDetails` tracks per-question state and is initialized on first run via `LoadDatabaseInBackgroundThread`
- **User state** (coins, settings, first-run flags) lives in SharedPreferences

### Package Structure
| Package | Role |
|---|---|
| `elements/` | ORM models: `GenericQuestion`, `GenericAnswerDetails` |
| `ui/` | Activities and Fragments |
| `ui/dialogs/` | Custom dialog classes |
| `adapters/` | RecyclerView/ListView adapters |
| `utils/` | 14 utility classes: `Constants`, `JSONUtils`, `CoinsHelper`, `AnalyticsHelper`, `SoundHelper`, etc. |
| `billingUtils/` | In-app purchase (IAB) library |
| `callbacks/` | `QuestionsCallback` interface |

### Key Utilities
- `Constants.java` — Game mode IDs (0/1/2), status codes, coin prices (hint=100, solution=200, unlock=125, ad reward=150), hardcoded question counts (50 per category)
- `CharacterHelper.java` — Large (~39KB) utility managing character/avatar state
- `PerplexyApplication` — Extends `SugarApp`; initializes Google Analytics tracker

## Dependencies & Patterns

- **View binding**: ButterKnife 7.0.1 (`@Bind`, `@OnClick`)
- **JSON parsing**: LoganSquare 1.3.6 (annotation processor via APT)
- **ORM**: Sugar ORM 1.5 (domain package: `com.contextgenesis.perplexy.elements`)
- **Ads**: Google Play Services AdMob + Unity Ads (rewarded video)
- **Billing**: Google Play IAB helper in `billingUtils/`

## Notable Codebase Characteristics

- **ViewBinding** is used across all Activities, Fragments, and Dialogs — no ButterKnife
- **Room** (v2.6.1) is used for local persistence via `AppDatabase` singleton; `allowMainThreadQueries()` is set — async migration is a future TODO
- **Gson** is used for JSON parsing of question assets
- **Firebase Analytics** replaces the old Google Analytics SDK
- **Play Billing Library v7** is wired up in `BankDialog` but IAP call sites remain commented out
- `minifyEnabled false` in release builds — ProGuard rules are present if it's ever enabled
- Test coverage is minimal — placeholder tests only
