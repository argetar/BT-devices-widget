# Codex task: bt-inventory-state-action-vertical-slice

## Context

Project: **BT devices widget**

Repo:

`/home/eli/BT-devices-widget`

GitHub:

`https://github.com/argetar/BT-devices-widget.git`

Current state:

`/home/eli/BT-devices-widget/.hermes/state.md`

Accepted prior gates:

1. `bt-api-feasibility-spike` — `PASS_WITH_WARNINGS`
   - Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-bt-api-feasibility-spike-review.md`
   - Key conclusion: API 26–35 supports paired inventory and public A2DP/headset state observation, but no reliable public system connect/disconnect toggle for Android 14 MVP.
2. `android-toolchain-gradle-ci-setup` — `PASS_WITH_WARNINGS`
   - Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-android-toolchain-gradle-ci-setup-review.md`
   - CI evidence: `https://github.com/argetar/BT-devices-widget/actions/runs/31050602768`
   - `assembleDebug`, 8 JVM tests, and `lintDebug` passed.

Primary test device:

- Honor 200 Pro / Android 14 / MagicOS 8.

First MVP remains:

- selected Bluetooth devices;
- connection-state display;
- tap selected device to connect/disconnect when feasible;
- Android 14 fallback must be visible Activity + Bluetooth Settings fallback when public direct toggle is unavailable.

## Goal

Implement a narrow interface-backed vertical slice that provides the foundation for selected-device inventory, public audio-profile state observation, widget/action permission handling, and Settings fallback.

This is the first Android runtime slice, but it must remain narrow and fail-closed.

## Required scope

Implement only:

1. **Interfaces / models**
   - bonded device inventory abstraction;
   - connection state source abstraction;
   - selected-device local storage abstraction;
   - action launcher/decision integration;
   - data models that avoid logging/transmitting MAC addresses.

2. **Permission-aware bonded inventory**
   - API-aware use of `BLUETOOTH_CONNECT` for API 31+;
   - legacy behavior for API 26–30;
   - fail closed when adapter unavailable, Bluetooth disabled, permission missing, or selected device is stale/unbonded;
   - do not request `BLUETOOTH_SCAN` or location for paired-only inventory.

3. **Public A2DP/headset state observation**
   - interface-backed Android adapter for querying `BluetoothA2dp` and `BluetoothHeadset` state where permission allows;
   - treat generic/HID state conservatively as unknown/best effort;
   - combine A2DP/headset state with a clear aggregation rule such as “connected if any supported audio profile is connected”.

4. **Broadcast invalidation**
   - documented receiver or receiver skeleton for Bluetooth/profile state broadcasts;
   - treat broadcasts as invalidation signals only, then re-query;
   - avoid long-running receiver work and avoid background service unless separately justified.

5. **Visible ActionActivity / Settings fallback**
   - add a minimal visible Activity used by widget taps or future UI to handle permission/Bluetooth-disabled state and unsupported direct toggle requests;
   - if `BLUETOOTH_CONNECT` is missing, request/explain from Activity;
   - if direct system toggle is unavailable on Android 14/API 26–35, open system Bluetooth settings or a safe settings intent;
   - never claim direct connect/disconnect occurred if only settings fallback was opened.

6. **Local selected-device storage**
   - minimal local store for user-selected devices, enough to support future widget rendering;
   - no cloud/network, no secrets, no shared logs with raw MAC addresses;
   - revalidate selected devices against current bonded inventory.

7. **Tests**
   - JVM tests for permission/state/action/inventory decision logic and storage serialization where possible;
   - keep Android framework code behind interfaces so core logic remains testable;
   - update existing decision tests if needed.

## Manual test documentation

Add or update documentation for the Honor 200 Pro manual matrix. It should include:

- permission grant/deny path;
- Bluetooth off/on path;
- bonded audio devices: earbuds/headset and speaker;
- optional generic device such as keyboard/mouse;
- connect/disconnect in system settings then app/widget state refresh;
- stale selection after unpair;
- process death/relaunch;
- no MAC addresses in shared logs.

Do not claim manual tests were run unless they were actually run on the device.

## Required CI/build checks

Run locally if toolchain is available; otherwise rely on CI after Hermes push but still run static local checks.

Codex must run and report:

- `git diff --check`;
- static secret scan;
- static scan for hidden reflection, Shizuku/root, Accessibility automation, app network calls, and process execution;
- remote URL token check;
- `git status --short --branch`.

If local Android toolchain is available, also run:

- `gradle --no-daemon --stacktrace assembleDebug` or `./gradlew ...` if wrapper exists;
- `gradle --no-daemon --stacktrace testDebugUnitTest`;
- `gradle --no-daemon --stacktrace lintDebug`.

If local toolchain remains unavailable, document exact blocker. Hermes will push and verify GitHub Actions CI.

## Strictly forbidden

Do not add:

- full widget grid UI;
- polished device-selection UI;
- direct system connect/disconnect experiment;
- hidden API reflection as runtime/default path;
- Shizuku/root/privileged behavior;
- Accessibility automation;
- app network calls;
- credentials/secrets;
- Gradle wrapper JAR or action pin upgrades in this gate unless absolutely required for build correctness and separately justified;
- Play Store claims beyond the reviewed feasibility conclusions.

## Expected files

Likely create/modify:

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/*`
- `app/src/main/java/cz/argetar/btdeviceswidget/action/*`
- `app/src/main/java/cz/argetar/btdeviceswidget/storage/*`
- `app/src/test/java/cz/argetar/btdeviceswidget/.../*Test.kt`
- `docs/honor-200-pro-manual-test-matrix.md` or update existing docs
- `.hermes/results/codex-bt-inventory-state-action-vertical-slice-result.md`

## Result file

Save full result to:

`/home/eli/BT-devices-widget/.hermes/results/codex-bt-inventory-state-action-vertical-slice-result.md`

The result must include:

- summary;
- files changed;
- architecture/adapter boundaries;
- permission/state/fallback behavior;
- tests/checks actually run;
- local build blocker if any;
- commit SHA;
- non-secret/non-push confirmation;
- recommended next gate.

Commit locally if clean. Do not push.
