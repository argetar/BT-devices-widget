# Crestodian review brief: BT API feasibility spike

## Review target

Repository:

`/home/eli/BT-devices-widget`

GitHub:

`https://github.com/argetar/BT-devices-widget.git`

HEAD commit:

`bf975b92cb036c3241effde37c0eba949e21731b`

Implementation commit:

`1dcbe9ffbab7c69983c07f61d8ba0bd7bf0388df`

Result/report commit:

`bf975b92cb036c3241effde37c0eba949e21731b`

Codex result:

`/home/eli/BT-devices-widget/.hermes/results/codex-bt-api-feasibility-spike-result.md`

## Product context

App: **BT devices widget**

First MVP goal:

- selected Bluetooth device list;
- connection-state display;
- tap-to-connect/disconnect when feasible;
- widget grid/customization later;
- audio-specific features later, not first MVP.

Primary target phone: Honor 200 Pro / Android 14 / MagicOS 8, but design should not be phone-specific. Future goal: private APK first, Play Store-compatible path later.

Reference apps from user phone:

- Bluetooth Audio Connect Widget
- Bluetooth AudioWidget+

## Gate scope

Review a technical feasibility spike, not a full app implementation.

This gate should determine whether the conclusions and foundation are technically sound:

- Android permissions;
- bonded device inventory;
- connection state observation;
- connect/disconnect options;
- widget action constraints;
- Play Store-compatible MVP recommendation.

## Changed files

```text
.hermes/results/codex-bt-api-feasibility-spike-result.md
app/build.gradle.kts
app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothActionDecision.kt
app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothCapabilityMatrix.kt
app/src/test/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothActionDecisionTest.kt
docs/bluetooth-api-feasibility.md
```

No full widget UI, selection UI, Android Bluetooth wrapper, service, hidden API implementation, Shizuku/root, Accessibility automation, or network behavior should be present.

## Claimed conclusion

Codex concluded:

- API 26-35 normal Play-compatible apps can list bonded devices and query/observe public audio-profile state after permissions.
- API 26-35 do not expose a reliable public SDK connect/disconnect operation for system-managed A2DP, headset, HID, or generic bonded devices.
- A2DP/headset should be first-class for state observation.
- Generic/HID state should remain conservative/unknown or best-effort.
- Widget tap should route through a visible Activity for permission/Bluetooth-disabled handling and system Bluetooth settings fallback when direct toggle is unavailable.
- Hidden API reflection, Shizuku/root, Accessibility automation, and private privileged paths are out of profile for Play-compatible MVP.
- API 37 public `BluetoothDevice.connect()` / `disconnect()` with companion association is a future gated candidate, not Android 14 capability.

## Hermes verification performed

```text
Result file read: OK
Git state before push: ahead 2 from origin/main
Changed files reviewed: OK
git diff --check origin/main..HEAD: OK
Secret scan: 0 findings
Static app capability scan for network/reflection/Shizuku/Accessibility/root/process execution: 0 actionable findings
Remote URL token scan: OK, no embedded token
Push: 8b1127f..bf975b9 main -> main
remote_matches_local=yes
```

Build/toolchain caveat independently verified by Hermes:

```text
java: missing
gradle: missing
kotlinc: missing
ANDROID_HOME: missing
ANDROID_SDK_ROOT: missing
gradle_wrapper: missing
```

Therefore Hermes did not run Android build or JUnit tests locally. Codex also did not claim build/test execution. This is acceptable for the spike only if the reviewer agrees the code changes are conservative and the next gate includes Android toolchain/build setup or CI.

## Review questions

Please return `PASS`, `PASS_WITH_WARNINGS`, or `BLOCKED`.

Focus on:

1. Is the Android Bluetooth API feasibility conclusion accurate for API 26-35 / Android 14?
2. Is the API 37/companion-device future candidate described correctly and not overclaimed?
3. Is the recommendation to use visible Activity + settings fallback technically and product-wise sound?
4. Does the decision model encode safe fail-closed behavior without accidentally enabling hidden/private paths?
5. Is the first MVP scope still aligned with user needs, despite the direct-toggle limitation claim?
6. Is it acceptable that build/JUnit execution is blocked locally due to missing toolchain for this spike?
7. What should be the next gate: Android toolchain/CI setup, permission-aware inventory/state adapter, or something else?

## Expected output

Save full review to:

`/home/eli/BT-devices-widget/.hermes/reviews/crestodian-bt-api-feasibility-spike-review.md`

Telegram final should include only:

- verdict;
- review file path;
- blocking issues, if any;
- warning impact;
- recommended next gate.
