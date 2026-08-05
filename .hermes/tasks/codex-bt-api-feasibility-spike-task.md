# Codex task: bt-api-feasibility-spike

## Context

Project: **BT devices widget**

Repo:

`/home/eli/BT-devices-widget`

GitHub:

`https://github.com/argetar/BT-devices-widget.git`

Current status:

- Android scaffold exists but Bluetooth behavior is not implemented yet.
- Primary MVP target phone: Honor 200 Pro / Android 14 / MagicOS 8, but app should not be hardcoded to this phone.
- Planned support baseline: Android 8.0+ / API 26, targetSdk 35, unless this spike proves a different baseline is necessary.
- Distribution: private/sideload APK first, but keep future Play Store compatibility where feasible.
- Reference apps seen on user phone:
  - Bluetooth Audio Connect Widget
  - Bluetooth AudioWidget+
- Future audio-specific functions are desirable, but **not first MVP**.

First MVP scope:

- selected Bluetooth device list;
- connection-state display;
- tap selected device to connect/disconnect according to current state;
- widget grid and customization come later.

## Goal

Perform a focused technical feasibility spike for Bluetooth device discovery, connection state, and connect/disconnect behavior on modern Android.

This is not the full app implementation. Produce a concrete implementation recommendation and a small, testable foundation for the later MVP.

## Key product question

Can a normal Android app/widget reliably connect/disconnect selected Bluetooth devices, especially audio devices, in a Play Store-compatible way?

If yes, define the exact API/profile path.
If partly, define the supported device/profile matrix.
If no for some classes, define a safe fallback, but do **not** make fallback the assumed primary behavior.

## Required investigation areas

Research and document Android behavior for:

1. **Permissions**
   - Android 12+ `BLUETOOTH_CONNECT`;
   - older `BLUETOOTH` / `BLUETOOTH_ADMIN` maxSdk behavior;
   - whether `BLUETOOTH_SCAN` is needed for bonded-device listing or only discovery;
   - runtime permission UX for widget-triggered actions.

2. **Bonded device inventory**
   - listing paired/bonded devices;
   - stable identifiers / MAC address handling and privacy restrictions;
   - storing selected devices safely.

3. **Connection state**
   - how to detect connected/disconnected state for:
     - A2DP/audio sink;
     - headset/handsfree;
     - HID/keyboard/mouse where possible;
     - generic bonded devices;
   - broadcasts vs active profile proxy queries;
   - widget refresh triggers.

4. **Connect/disconnect actions**
   - public APIs available directly;
   - Bluetooth profile proxy APIs (`BluetoothProfile`, `BluetoothA2dp`, `BluetoothHeadset`, etc.);
   - whether connect/disconnect are public, hidden, reflection-based, or unavailable on current SDK;
   - Play Store compatibility of any required approach;
   - OEM/Android-version risks;
   - whether reference apps likely rely on audio-profile behavior only.

5. **Widget execution constraints**
   - `PendingIntent` receiver/activity/service constraints;
   - background execution limitations;
   - permission denial handling from widget tap;
   - app open/config fallback if runtime permission is missing.

## Expected implementation output

Add design/probe artifacts only; do not build the full widget UI yet.

Suggested files:

- Create: `docs/bluetooth-api-feasibility.md`
- Create: `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothCapabilityMatrix.kt`
- Create: `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothActionDecision.kt`
- Create tests as feasible under `app/src/test/...` for pure decision logic.

The code should be a foundation, not a full hidden-API implementation.

Minimum code expectations:

- a sealed/result model for action capability, e.g. supported direct toggle / supported state-only / requires settings fallback / unsupported / permission missing;
- a pure decision function that maps Android SDK/version, granted permissions, profile type, bonded/connected state, and available implementation method to an action plan;
- unit tests for the decision matrix;
- no hidden API reflection unless documented and isolated behind a disabled/experimental strategy. Prefer documenting hidden/reflection approaches rather than enabling them in MVP code.

## Documentation requirements

`docs/bluetooth-api-feasibility.md` must include:

- summary recommendation;
- Android version/permission matrix;
- device/profile support matrix;
- connect/disconnect implementation options;
- Play Store compatibility assessment;
- risks and unknowns;
- recommended MVP behavior;
- manual test plan for Honor 200 Pro with 2-3 real devices;
- recommendation for next gate.

Explicitly answer:

- Should MVP focus first on audio devices while preserving generic device display?
- Can widget tap directly connect/disconnect, or does it need an Activity/Service hop?
- What happens when permission is missing?
- Is `minSdk 26` still reasonable?
- What is the cleanest future path to Play Store?

## Constraints

Do **not**:

- implement the full widget UI;
- implement final device-selection UI;
- add hidden API reflection as the default path;
- add Shizuku/root/privileged behavior;
- add Accessibility automation;
- add network calls in app code;
- read secrets;
- commit credentials;
- push to git.

Allowed:

- docs;
- pure Kotlin decision/model code;
- tests for decision logic;
- small Android API wrapper skeleton if it compiles cleanly;
- build/CI adjustments if necessary and safe.

## Required checks

Run and report what is available in this environment:

- Gradle/Android build or clearly document blocker if Java/Gradle/Android SDK is unavailable;
- unit tests if build toolchain is available;
- static scan for secrets/credentials;
- `git diff --check`;
- `git status --short --branch`;
- no token in remote URL.

If local toolchain is unavailable, do not fake build output. Document the blocker and keep code changes conservative.

## Result file

Save full result to:

`/home/eli/BT-devices-widget/.hermes/results/codex-bt-api-feasibility-spike-result.md`

The result must include:

- summary;
- files changed;
- implementation recommendation;
- permission/profile/action support matrix;
- tests/checks actually run;
- build blockers if any;
- commit SHA;
- non-secret/non-push confirmation;
- recommended next gate.

Commit locally if clean. Do not push.
