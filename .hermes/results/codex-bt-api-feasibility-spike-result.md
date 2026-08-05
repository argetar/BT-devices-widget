# Result: bt-api-feasibility-spike

## Summary

Completed the Android Bluetooth feasibility spike as design plus a small pure-Kotlin capability/action foundation. The Android 14 conclusion is fail-closed: a normal application can list paired devices and observe public audio-profile state after permission, but API 26–35 has no public SDK operation for directly connecting or disconnecting system-managed A2DP, headset, HID, or generic bonded devices.

The recommended MVP is audio-first for accurate state while preserving generic bonded-device display. A widget tap should enter a small visible action Activity, handle permission/Bluetooth state, and open system Bluetooth settings when a system-profile toggle is requested. Hidden API reflection, Shizuku/root, Accessibility automation, and app network behavior were not added.

## Files changed

- `docs/bluetooth-api-feasibility.md`: recommendation, primary Android references, permission/profile/action matrices, widget architecture, Play compatibility, risks, Honor 200 Pro test plan, and next gate.
- `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothCapabilityMatrix.kt`: framework-independent product capability facts by profile.
- `app/src/main/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothActionDecision.kt`: sealed action plans and a pure, fail-closed decision function.
- `app/src/test/java/cz/argetar/btdeviceswidget/bluetooth/BluetoothActionDecisionTest.kt`: eight decision/matrix unit tests.
- `app/build.gradle.kts`: JUnit 4 test dependency.
- `.hermes/results/codex-bt-api-feasibility-spike-result.md`: this report.

No widget UI, device-selection UI, Android Bluetooth wrapper, service, or runtime behavior was implemented.

## Implementation recommendation

1. Keep `minSdk 26`, `compileSdk/targetSdk 35`, and paired-only inventory for the first MVP.
2. On API 31+, request runtime `BLUETOOTH_CONNECT` from a visible Activity. Do not request `BLUETOOTH_SCAN` until active discovery is an actual feature.
3. Query A2DP and headset profile proxies for selected audio devices. Use documented broadcasts only as invalidation triggers, then re-query. Generic/HID state remains `unknown` or best effort rather than making a false connected claim.
4. Route widget taps through a minimal `ActionActivity`. Missing permission opens its permission UX; Bluetooth-off or unsupported system-profile actions open system Bluetooth settings.
5. Persist only user-selected local identifiers, avoid logs/network transmission, and revalidate bond membership every time.
6. Treat API 37 public `BluetoothDevice.connect()` / `disconnect()` with a companion-device association as a future gated candidate, not an Android 14 capability.

## Permission/profile/action support matrix

| Scope | State/inventory | Direct action | MVP outcome |
|---|---|---|---|
| API 26–30 paired devices | Legacy `BLUETOOTH`; no scan required | No public general system-profile toggle | Inventory/state plus Settings fallback |
| API 31–35 paired devices | Runtime `BLUETOOTH_CONNECT`; no `SCAN` for bonded-only list | No public A2DP/headset/HID/general toggle | Audio state first, generic display, Settings fallback |
| A2DP / headset | Public profile proxy query and documented broadcasts | Hidden profile methods are not allowed | Reliable per-profile state only |
| HID / generic bonded | Bond state and best-effort ACL signals | No dependable public general toggle | Display with conservative/unknown state |
| App-owned BLE GATT | App callback state | App can connect/disconnect its own GATT session | Technically supported but outside first MVP |
| API 37+ associated device | Public device API is a documented candidate | Public device connect/disconnect requires association or privilege | Future companion-device gate and device validation |

## Tests/checks actually run

- `git diff --cached --check` before implementation commit: passed.
- `git diff --check` after changes: passed.
- Static credential scan for private-key headers and common GitHub/Google/AWS/token/password assignment patterns: passed, no match.
- Static app capability scan for network APIs, reflection, Shizuku, Accessibility service, root/process execution: passed, no match.
- Remote inspection: `origin` is `https://github.com/argetar/BT-devices-widget.git`; no embedded token pattern.
- Source review: eight pure-decision JUnit tests added for permission denial, Android 14 audio fallback, disabled hidden method, API 37 association gating, app-owned GATT connect/disconnect, generic unknown state, transition suppression, and matrix policy.
- Final `git status --short --branch`: clean after the report commit, branch ahead of `origin/main`; no push performed.

## Build blockers, if any

The environment has no `java`, `gradle`, `kotlinc`, Gradle wrapper, `ANDROID_HOME`, or `ANDROID_SDK_ROOT`. Consequently neither the Android Gradle build nor JUnit execution was available. No build/test result was fabricated. Changes were limited to conservative framework-independent Kotlin and documentation; build and unit-test execution remain required in an Android toolchain environment.

## Commit

Implementation commit: `1dcbe9ffbab7c69983c07f61d8ba0bd7bf0388df`.

This tracked report is committed in a follow-up local commit so that the repository can finish clean. Its containing commit is reported in the final handoff (a commit cannot literally contain its own SHA).

## Non-secret/non-push confirmation

No secrets or credentials were read or added. No network calls were added to app code. No hidden API reflection, privileged behavior, Accessibility automation, or service restart was used. The remote URL contains no token. Nothing was pushed.

## Recommended next gate

Add an interface-backed Android adapter for permission-aware bonded inventory, A2DP/headset state query, documented broadcast invalidation, local selected-device storage, and the visible Activity Settings fallback. Run its unit/instrumented tests with a configured Android toolchain, then execute the documented manual matrix on Honor 200 Pro with earbuds/headset, a speaker, and optionally a keyboard/mouse before beginning widget-grid or selection UI work.
