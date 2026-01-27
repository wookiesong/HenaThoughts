# HenaThoughts - Implementation Tasks

## Phase 1: Project Scaffolding
- [x] Task 1.1: Explore original repo structure
- [x] Task 1.2: Write spec.md
- [x] Task 1.3: Create Gradle project structure (build.gradle, settings.gradle, gradle wrapper)
- [x] Task 1.4: Configure app/build.gradle (dependencies: Compose, Material3, Gson)
- [x] Task 1.5: Create AndroidManifest.xml with permissions and resources

## Phase 2: Data Layer
- [x] Task 2.1: Bundle `philosophies.json` in `res/raw/`
- [x] Task 2.2: Create `Philosophy.kt` data class
- [x] Task 2.3: Create `PhilosophyManager.kt` (load bundled JSON, read/write user JSON, CRUD operations)

## Phase 3: Notification System
- [x] Task 3.1: Create `SettingsManager.kt` (SharedPreferences: enabled, interval, active hours)
- [x] Task 3.2: Create `AlarmScheduler.kt` (schedule/cancel exact alarms, respect active hours)
- [x] Task 3.3: Create `NotificationReceiver.kt` (pick random philosophy, show notification, reschedule)
- [x] Task 3.4: Create `BootReceiver.kt` (restore alarm on reboot)

## Phase 4: UI
- [x] Task 4.1: Create `MainActivity.kt` with Compose scaffold and navigation
- [x] Task 4.2: Main screen - category-grouped philosophy list with edit/delete actions
- [x] Task 4.3: Add/Edit screen - form for category, title, content
- [x] Task 4.4: Settings section - notification toggle, interval picker, active hours

## Phase 5: Integration & Polish
- [x] Task 5.1: Wire notification permission request on first launch
- [x] Task 5.2: Notification tap opens MainActivity
- [x] Task 5.3: App icon resources (mipmap)
- [x] Task 5.4: Final review and consistency check

## Dependencies
- Phase 2 depends on Phase 1
- Phase 3 depends on Phase 2 (PhilosophyManager)
- Phase 4 depends on Phase 2 + Phase 3 (SettingsManager)
- Phase 5 integrates all phases

## Decision Points (user input needed)
- After Phase 1: Confirm project config before writing code
- After Phase 3: Verify notification logic before UI
- After Phase 4: Review UI before final polish
