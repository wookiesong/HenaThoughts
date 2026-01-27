# HenaThoughts - Minimal Rebuild Spec

## Overview
Kotlin/Jetpack Compose Android app that periodically shows philosophical thoughts as notifications.
**Private use only** - not intended for public distribution.

## Core Requirement
- Load philosophies from bundled `res/raw/philosophies.json`
- Periodically display a random philosophy as an Android notification
- Notification tap opens the app

## Data Model
```kotlin
data class Philosophy(val id: Int, val category: String, val title: String, val content: String)
```

## JSON Source
- 41 entries, 11 categories, Korean text
- Bundled as `res/raw/philosophies.json` (read-only, no CRUD in minimal version)

## Notification System
- AlarmManager with exact alarms for periodic scheduling
- BroadcastReceiver to handle alarm and show notification
- BootReceiver to restore schedule after device reboot
- Settings: interval (default 3h), active hours (default 08:00-21:00)

## Philosophy CRUD
- Add new philosophy (category, title, content)
- Edit existing philosophy
- Delete philosophy
- User modifications saved to internal storage (`user_philosophies.json`)
- On first launch, copy bundled JSON to internal storage as baseline

## UI (Minimal)
- Main screen: list of philosophies grouped by category, with edit/delete
- Add/Edit screen: form for category, title, content
- Settings section: notification on/off, interval

## Permissions
- `POST_NOTIFICATIONS` (Android 13+)
- `RECEIVE_BOOT_COMPLETED`
- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`

## Tech Stack
- Kotlin, Jetpack Compose (Material3)
- Gson for JSON parsing
- minSdk 26, targetSdk 34, compileSdk 34

## Out of Scope (for minimal version)
- Theme color customization
- Sequential mode (random only)
- Daily max notification count
- User-modified JSON persistence
