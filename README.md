# Dharma Path — Android App

A spiritual companion Android app for daily devotion, mantra practice, puja guidance, and cosmic insights. Built with Kotlin, XML Views, and the Navigation Component.

---

## Features

- **Home** — Greeting, daily Panchang (tithi, nakshatra, sunrise/sunset, muhurat), stats, and quick action orbs
- **Jaap Mala** — Digital mantra counter with progress ring, goal selector (108/216/540/1080), elapsed timer, and session history
- **AI Palmistry** — Upload a palm photo for Vedic line analysis
- **Puja Vidhi** — Searchable library of step-by-step puja guides with samagri checklist and vidhi steps
- **Profile** — User stats, milestones, theme toggle (dark/light), and settings
- **Subscription** — 3-tier plan selector (Seeker / Devotee / Sadhak) with monthly/annual billing toggle
- **Onboarding** — 5-step flow with personalization (name, city, deity)
- **Login** — Phone + OTP authentication flow

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML Layouts + ViewBinding |
| Navigation | Navigation Component (Fragment-based) |
| Architecture | MVVM — ViewModel + StateFlow |
| Persistence | DataStore Preferences |
| Image loading | Coil |
| HTTP | OkHttp |
| JSON | Gson |
| Theme | DayNight via `AppCompatDelegate` |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Project Structure

```
app/src/main/
├── java/com/paid/myapplication/
│   ├── MainActivity.kt               # Single activity, NavHostFragment + BottomNav
│   ├── data/
│   │   ├── Models.kt                 # Data classes (JaapStats, PujaItem, etc.)
│   │   └── PrefsStore.kt             # DataStore wrapper
│   ├── viewmodel/
│   │   └── AppViewModel.kt           # Shared ViewModel for all fragments
│   └── ui/
│       ├── fragments/                # One Fragment per screen
│       │   ├── HomeFragment.kt
│       │   ├── JaapFragment.kt
│       │   ├── PalmReadingFragment.kt
│       │   ├── PujaFragment.kt
│       │   ├── PujaDetailFragment.kt
│       │   ├── ProfileFragment.kt
│       │   ├── SubscriptionFragment.kt
│       │   ├── OnboardingFragment.kt
│       │   └── LoginFragment.kt
│       └── views/
│           └── JaapRingView.kt       # Custom Canvas view for mala progress ring
└── res/
    ├── layout/                       # XML layouts for each fragment
    ├── navigation/nav_graph.xml      # Navigation graph
    ├── drawable/                     # Shape drawables (theme-aware)
    ├── values/colors.xml             # Light theme colors
    ├── values-night/colors.xml       # Dark theme colors
    └── values/themes.xml             # DayNight theme
```

---

## Build & Run

**Requirements:**
- Android Studio Hedgehog or newer
- JDK 11+
- Android SDK 36

**Steps:**
1. Open the project root in Android Studio
2. Let Gradle sync complete
3. Run on a device or emulator (API 24+)

```bash
./gradlew assembleDebug
```

---

## Theme Switching

Dark/light mode is persisted in DataStore and applied via `AppCompatDelegate.setDefaultNightMode()` on app start. Toggling from the Profile screen takes effect immediately by recreating the activity. All colors are defined in `values/colors.xml` (light) and `values-night/colors.xml` (dark).

---

## Jaap Counter

Tap the ring to increment the count — one tap, one mantra. The Play/Pause button controls only the elapsed time timer. The counter never auto-increments. When the goal is reached, a completion dialog appears and the session is saved to history.
