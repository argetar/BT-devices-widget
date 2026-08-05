# Bluetooth API feasibility spike

## Recommendation

The Android 14 MVP should be **audio-first for accurate state**, while still showing selected bonded devices generically. A normal app can list bonded devices and observe A2DP/headset state with public APIs after `BLUETOOTH_CONNECT` is granted. It cannot, on API 26–35, reliably connect or disconnect system-managed A2DP, headset, HID, or arbitrary bonded devices using public SDK APIs.

Therefore a tap on Honor 200 Pro / Android 14 should refresh state and open system Bluetooth settings when a system-profile connection change is needed. This is a real product limitation, not an implementation gap. Hidden profile methods, reflection, root, Shizuku, and Accessibility automation are out of profile. App-owned BLE GATT sessions are independently controllable, but they do not toggle the phone's system audio/HID connection.

Keep `minSdk 26`: it covers the intended baseline without changing the core limitation. The clean Play Store path is public state APIs now, a transparent Settings fallback, and a future capability gated on Android API 37's public `BluetoothDevice.connect()` / `disconnect()` plus a `CompanionDeviceManager` association. That future path needs device testing and a target/compile SDK upgrade before adoption.

Primary sources: [Bluetooth permissions](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions), [find Bluetooth devices](https://developer.android.com/develop/connectivity/bluetooth/find-bluetooth-devices), [BluetoothProfile](https://developer.android.com/reference/android/bluetooth/BluetoothProfile), [BluetoothA2dp](https://developer.android.com/reference/android/bluetooth/BluetoothA2dp), [BluetoothHeadset](https://developer.android.com/reference/android/bluetooth/BluetoothHeadset), [BluetoothDevice](https://developer.android.com/reference/android/bluetooth/BluetoothDevice), and [advanced app-widget behavior](https://developer.android.com/develop/ui/views/appwidgets/advanced).

## Permissions by Android version

| Version | Bonded inventory and profile state | Active discovery | MVP choice |
|---|---|---|---|
| API 26–30 | Manifest `BLUETOOTH`; `BLUETOOTH_ADMIN` for discovery/admin operations | Location permission is implicated by discovery results | Declare legacy permissions with `maxSdkVersion=30`; do not actively scan |
| API 31+ | Runtime Nearby devices `BLUETOOTH_CONNECT` for bonded-device data and communicating with paired devices | Runtime `BLUETOOTH_SCAN`; location rules depend on whether scan results derive location | Request only `BLUETOOTH_CONNECT` for paired-only MVP; add `SCAN` only with a real discovery feature |

`BluetoothAdapter.getBondedDevices()` is the paired inventory path and does not justify active scanning. Permission must be requested from visible UI. A widget receiver cannot display a runtime permission dialog reliably, so a tap with missing permission launches a small Activity that explains and requests Nearby devices access. Denial leaves the widget in a clear “permission required” state and offers the same Activity; it never loops or silently fails.

Store only selections the user chose. On current supported versions, normalize and locally persist the device address as the lookup key after permission is granted, with an optional user label. Treat address/name as personal device data: do not log or transmit them, and verify that the device remains bonded on every use. Do not use `BluetoothDevice.toString()` as an identifier (newer Android may redact it). A future companion-device flow should prefer its association identity and lifecycle.

## Profile and state support

| Device/profile | Public state source | Reliability | MVP treatment |
|---|---|---|---|
| Classic A2DP audio | `BluetoothProfile` proxy / `BluetoothA2dp.getConnectionState()` and profile state broadcasts | Good for profile connection; a device can have multiple profile states | First-class selected device/state |
| Headset / hands-free | `BluetoothHeadset` proxy and profile state broadcasts | Good for that profile; not equivalent to A2DP media state | First-class, combine with A2DP carefully |
| LE Audio | Public profile APIs on newer releases, with device/OEM variation | Requires version and real-device validation | Observe later; not MVP promise |
| HID keyboard/mouse | ACL broadcasts can hint at link state; no dependable general public HID-host per-device contract for this MVP | Best effort only | Generic display, state marked unknown where needed |
| Generic bonded device | Bond state plus ACL connect/disconnect broadcasts | Bonded is not connected; ACL is not an application/profile semantic | Display only, no strong connected claim |
| App-owned BLE GATT | `BluetoothGattCallback` for the app's own session | Reliable for that session only | Out of first MVP unless a supported peripheral protocol is added |

On refresh, obtain short-lived profile proxies with `getProfileProxy`, query selected devices, then close proxies. Register exported-safe receivers only for documented Bluetooth broadcasts and refresh affected widgets. Treat broadcasts as invalidation signals and re-query current state; do not assume ordered delivery. Represent `connecting`, `connected`, `disconnecting`, `disconnected`, and `unknown`, and debounce taps during transitions.

## Action options and Play compatibility

| Path | API 26–35 | API 37+ candidate | Play-compatible recommendation |
|---|---|---|---|
| A2DP/headset proxy `connect` / `disconnect` | Not public SDK methods | Superseded by device-level candidate | Do not reflect into hidden methods |
| HID or generic system connection toggle | No general public toggle | `BluetoothDevice.connect()` / `disconnect()` may apply | Only after associated-device, SDK, OEM, and policy validation |
| App-owned GATT session | Public `connectGatt`; app can close/disconnect its own client | Same | Valid only for a defined BLE protocol, not as an audio toggle |
| System Bluetooth settings | Public intent fallback | Public intent fallback | Supported, explicit fallback for MVP |

Current Android documentation marks the public `BluetoothDevice.connect()` and `disconnect()` APIs as added in API 37. They require `BLUETOOTH_CONNECT` and either privileged Bluetooth permission or an eligible `CompanionDeviceManager` association. They are unavailable to this project's compile SDK 35 and unavailable on the Android 14/API 34 target, so the pure decision model records them only as a future capability.

Reference apps named for this spike are audio-focused and may use legacy/hidden profile behavior, OEM affordances, or merely route users to settings. Their visible behavior is not evidence of a stable public API. The project should not imitate an unverifiable implementation path.

## Widget action architecture

Use an immutable/update-current `PendingIntent` from each widget cell to a minimal transparent `ActionActivity` carrying only an internal selection ID. The Activity performs the permission and Bluetooth-enabled checks, asks for permission when necessary, evaluates the capability decision, and either performs a supported action or opens Bluetooth settings. This user-visible hop is the safest permission UX and remains compatible with background launch rules initiated by a widget tap.

A manifest receiver may handle quick state invalidation and widget redraws, using `goAsync()` only for short bounded work. It must not hold a profile proxy, perform a long connection workflow, or start an unrestricted background service. A service is unnecessary for the feasibility foundation; if later work proves one necessary, foreground-service type and Android background-start restrictions require a separate design gate.

## Risks and unknowns

- MagicOS 8 may delay broadcasts, kill cached processes, or expose settings UI differently; public API behavior still needs an Honor-device probe.
- Audio devices frequently connect A2DP and headset profiles independently. Product state needs an explicit aggregation rule, likely “connected if any supported audio profile is connected” with detail available in the app.
- ACL state can be transient and cannot prove that a generic device is usable by its intended system profile.
- Widgets are eventually consistent. Broadcast-triggered refresh, foreground refresh, and periodic reconciliation must tolerate missed events.
- API 37 behavior, companion association UX, Play policy, and OEM implementation are future unknowns and must not be backported with reflection.
- Bluetooth-off and permission-denied states must remain distinguishable from disconnected.

## Honor 200 Pro manual test plan

Use a release-like debug build with airplane mode enabled except Bluetooth; no network is required.

1. Pair two audio devices in system settings: earbuds/headset supporting A2DP + hands-free, and a speaker supporting A2DP. Optionally pair a keyboard or mouse for the generic case.
2. Grant Nearby devices from the app Activity. Verify bonded inventory, stable reselection after process death/reboot, and no `BLUETOOTH_SCAN` or location prompt.
3. Connect and disconnect each audio device in system settings. Verify profile query and broadcast-driven states, including switching between both devices and separate A2DP/headset transitions.
4. Tap each selected device while disconnected and connected. On Android 14 verify the app opens the Bluetooth settings fallback and never claims a direct toggle occurred.
5. Revoke Nearby devices permission, tap the widget, and verify the Activity explains and requests it. Deny twice and verify a stable permission-required state without crashes or loops.
6. Turn Bluetooth off, reboot, unpair one selection, and force-stop/relaunch. Verify disabled, stale selection, and unknown generic states fail closed.
7. If using a keyboard/mouse, verify it is displayed but is not promised a reliable connected state or direct action.

Capture OS build, MagicOS build, device profiles, timestamps, and observed state transitions without recording MAC addresses in shared logs.

## Recommended MVP and next gate

The first implementation gate should add a small Android adapter for: permission-aware bonded inventory, A2DP/headset profile queries, documented broadcast invalidation, local selection storage, and the `ActionActivity` Settings fallback. Keep framework calls behind interfaces so the pure capability/action decision remains unit-testable. Add instrumented probes and run the manual plan on Honor 200 Pro before designing the grid or selection UI.

Do not claim one-tap connect/disconnect on Android 14. If direct toggling is a non-negotiable product requirement, make that a separately approved experimental/private-distribution decision with explicit maintenance and policy costs; it is not part of the Play-compatible MVP.
