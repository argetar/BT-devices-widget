# BT devices widget Implementation Plan

> **For Hermes:** Use gated file-first orchestration: Codex implements narrowly, Hermes verifies, Crestodian reviews. No ACK/handshake prompts.

**Goal:** Build an Android home-screen widget named **BT devices widget** that displays selected Bluetooth devices, shows connected state in green, and lets the user tap a device to connect/disconnect when Android APIs/permissions allow it.

**Architecture:** Native Android app in Kotlin with a classic `AppWidgetProvider` + `RemoteViews` widget. A small configuration activity stores selected bonded devices and widget appearance. Bluetooth state is monitored via broadcasts and refreshed on demand; connect/disconnect behavior must first be proven in a spike because public Android APIs are limited for arbitrary profile connection management.

**Tech Stack:** Kotlin, Android Gradle Plugin, Android App Widgets (`RemoteViews`), Bluetooth permissions (`BLUETOOTH_CONNECT` on Android 12+), SharedPreferences/DataStore, unit tests and instrumented tests where feasible.

---

## Current context / assumptions

- Repo: `https://github.com/argetar/BT-devices-widget.git`
- Local path: `/home/eli/BT-devices-widget`
- Initial scaffold is intentionally minimal and does not implement Bluetooth control yet.
- Current Hermes host does **not** have Java/Gradle/Android SDK installed, so local Android builds are currently blocked. Build verification should use GitHub Actions or a later local Android toolchain setup.
- Important feasibility risk: Android public APIs generally allow listing bonded devices and observing connection state with permissions, but programmatic connect/disconnect for arbitrary devices/profiles may be restricted or require profile-specific APIs, hidden APIs, OEM behavior, companion-device flows, or privileged/Shizuku/root approaches. This must be validated before promising one-tap toggle for all devices.

## Proposed MVP definition

MVP should be split into proof gates:

1. **Feasibility spike:** determine exactly what connect/disconnect is possible on target Android version/device for selected Bluetooth device types.
2. **Widget display MVP:** selected devices, compact grid up to 3 columns, connected = green, disconnected = neutral/gray.
3. **Action MVP:** tap behavior based on feasible API result: direct toggle where supported; otherwise open Bluetooth settings/device detail or show unsupported state.
4. **Customization:** widget background color/transparency and icon style.

## Open questions for user

1. Target phone model and Android version?
2. Device types: earbuds/headphones, car, watch, keyboard, speaker, other?
3. Is fallback acceptable if direct connect/disconnect is impossible for some devices, e.g. open Bluetooth settings or device details?
4. Should app be Play Store-compatible, or is sideload/private APK enough?
5. Is Shizuku/root/privileged mode acceptable if public API cannot toggle devices?
6. Preferred widget size(s): 2x1, 3x1, 4x2, resizable only?
7. Should there be multiple widget instances with different selected devices/colors?

---

## Step-by-step plan

### Task 1: Confirm Android toolchain/build strategy

**Objective:** Make the project buildable either locally or in CI.

**Files:**
- Modify/create: `.github/workflows/android.yml`
- Possibly add: Gradle wrapper files if generated from a trusted local/CI environment

**Steps:**
1. Decide local vs GitHub Actions build path.
2. If CI, add GitHub Actions workflow using JDK 17 and Gradle.
3. Verify `./gradlew assembleDebug` or `gradle assembleDebug` in CI.
4. Commit build infrastructure.

**Validation:** CI build green or documented local blocker.

### Task 2: Bluetooth API feasibility spike

**Objective:** Determine real connect/disconnect options for target Android/device classes.

**Files:**
- Create: `docs/bluetooth-api-feasibility.md`
- Create: `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothCapabilityProbe.kt`
- Test: `app/src/test/.../BluetoothCapabilityProbeTest.kt` if JVM-testable

**Steps:**
1. Document Android permission model for Android 12+ and older versions.
2. Check APIs for bonded devices, connection state broadcasts, profile proxies.
3. Test feasibility for A2DP/HEADSET/HID where applicable.
4. Define product behavior matrix: direct toggle, settings fallback, unsupported.

**Validation:** Written matrix approved before implementing tap actions.

### Task 3: Device selection/configuration UI

**Objective:** Let user choose up to N Bluetooth devices and set widget appearance.

**Files:**
- Create/modify: `MainActivity.kt`
- Create: `DeviceSelectionViewModel.kt` or simple Activity helper
- Create: storage model for selected devices

**Steps:**
1. Request Bluetooth permission when needed.
2. List bonded devices.
3. Allow selecting/reordering devices.
4. Store selected device MAC/name/alias/icon color settings.
5. Store widget transparency/color.

**Validation:** Manual/emulator UI test and unit tests for storage serialization.

### Task 4: Widget layout and rendering

**Objective:** Render selected devices in compact grid with up to three columns.

**Files:**
- Modify: `bt_devices_widget.xml`
- Create: layout variants if needed
- Modify: `BtDevicesWidgetProvider.kt`

**Steps:**
1. Compute grid columns: 1-3 columns depending on selected count/widget size.
2. Render icon + optional short label.
3. Green tint for connected devices; gray/neutral for disconnected.
4. Apply configured background transparency.

**Validation:** screenshot/manual widget test; unit tests for column calculation and color mapping.

### Task 5: Bluetooth state refresh

**Objective:** Keep widget state current.

**Files:**
- Create: `BluetoothStateRepository.kt`
- Create: broadcast receiver for connection state changes if needed
- Modify: manifest/widget provider

**Steps:**
1. Query current connection state using feasible APIs.
2. Subscribe to relevant Bluetooth broadcasts.
3. Refresh widget on broadcasts and manual tap.
4. Handle permission denied gracefully.

**Validation:** tests for state mapping; manual device connect/disconnect test.

### Task 6: Tap action behavior

**Objective:** Tap connected device to disconnect or disconnected device to connect where supported.

**Files:**
- Create: `BluetoothDeviceActionController.kt`
- Modify: widget provider PendingIntents

**Steps:**
1. Implement action based on feasibility matrix.
2. If direct connect/disconnect unsupported, fallback to Bluetooth settings/device detail or show explanatory toast/activity.
3. Update widget after action attempt.
4. Log safe, non-secret action result.

**Validation:** manual tests on real phone/device; unit tests for action decision matrix.

### Task 7: Polish/customization

**Objective:** Improve UX and appearance.

**Files:**
- UI resources and config storage

**Steps:**
1. Transparency slider.
2. Background color picker or predefined palette.
3. Optional per-device icon/alias.
4. Multiple widget instance support.

**Validation:** manual UI/widget checks.

### Task 8: Release packaging

**Objective:** Produce installable debug/release APK with documented limitations.

**Files:**
- `README.md`
- `docs/release-notes.md`
- CI artifacts

**Steps:**
1. Build APK.
2. Document required permissions and limitations.
3. Provide install/test instructions.

**Validation:** APK installs and widget can be placed on home screen.

---

## Risks and tradeoffs

- **Direct connect/disconnect risk:** likely the main blocker. Public Android API may not allow generic profile connect/disconnect for all devices.
- **Widget limitations:** `RemoteViews` limits dynamic UI/complex layouts. A collection widget may be needed for many devices; for a small grid, generated rows/slots are simpler.
- **Battery/background limits:** must rely on broadcasts and explicit updates, not frequent polling.
- **Permissions:** Android 12+ requires runtime `BLUETOOTH_CONNECT`; older versions use legacy permissions.
- **Play Store vs private APK:** hidden APIs/Shizuku/root would likely disqualify or complicate Play Store distribution.

## Recommended first implementation gate after your approval

Start with **Bluetooth API feasibility spike**, not UI polish. The success of one-tap connect/disconnect determines the whole product behavior.

Suggested Codex gate:

```text
bt-api-feasibility-spike
```

Deliverables:
- documented capability matrix;
- small probe module;
- tests for permission/action decision logic;
- no broad UI implementation yet.
