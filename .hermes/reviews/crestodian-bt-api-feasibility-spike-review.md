# Crestodian review: BT API feasibility spike

## Verdict

**PASS_WITH_WARNINGS**

The feasibility conclusion at commit `bf975b92cb036c3241effde37c0eba949e21731b` is technically sound and appropriately fail-closed for a Play-compatible API 26–35 / Android 14 MVP. No blocking issue was found within this spike gate.

## Target and scope integrity

- Reviewed target commit: `bf975b92cb036c3241effde37c0eba949e21731b`; implementation commit: `1dcbe9ffbab7c69983c07f61d8ba0bd7bf0388df`.
- The target contains only the declared design report, two framework-independent Kotlin decision/capability files, eight unit tests, a JUnit dependency, and the Codex result.
- Repository `HEAD` was later at `0c3e5d80c88529916c086bfc43b64c48746f315f`, containing review handoff files only. Conclusions here are based on the requested commit, not the later handoff commit.
- `git diff --check` passed for the reviewed change range.

## Feasibility findings

### Permissions and inventory

The permission split is correct:

- API 26–30 uses legacy `BLUETOOTH` and, where discovery/admin operations are performed, `BLUETOOTH_ADMIN`; the manifest bounds both to API 30.
- API 31+ requires runtime `BLUETOOTH_CONNECT` to communicate with already-paired devices and access their data.
- A paired-only MVP can use `BluetoothAdapter.getBondedDevices()` without requesting `BLUETOOTH_SCAN`; active discovery would be a separate feature and permission gate.

The visible Activity recommendation is sound. A widget action can launch an Activity through its user-initiated `PendingIntent`; runtime permission explanation/request and Bluetooth-disabled handling belong in visible UI, not in the widget receiver. Denial, missing adapter, disabled Bluetooth, stale bond membership, transitions, and unknown state are distinguished rather than collapsed into a false connected/disconnected result.

Primary evidence: [Bluetooth permissions](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions), [finding Bluetooth devices](https://developer.android.com/develop/connectivity/bluetooth/find-bluetooth-devices), and [advanced widget behavior](https://developer.android.com/develop/ui/views/appwidgets/advanced).

### State observation and action matrix

The audio-first recommendation matches the public SDK:

- `BluetoothA2dp` and `BluetoothHeadset` expose profile connection-state queries and documented state broadcasts; these support observation but do not expose public profile `connect`/`disconnect` methods to normal apps on API 26–35.
- Bond state is not connection state. ACL broadcasts are only a conservative invalidation/best-effort signal for generic/HID devices, so `unknown` is the correct fallback.
- An app-owned BLE GATT connection is controllable only as that app's own client session and must not be represented as a system audio/HID toggle.
- Opening public system Bluetooth settings for a requested system-profile action is therefore the honest Android 14 fallback. The MVP remains useful for selected-device inventory and accurate audio state, but it cannot promise one-tap direct connect/disconnect.

Primary evidence: [BluetoothProfile](https://developer.android.com/reference/android/bluetooth/BluetoothProfile), [BluetoothA2dp](https://developer.android.com/reference/android/bluetooth/BluetoothA2dp), [BluetoothHeadset](https://developer.android.com/reference/android/bluetooth/BluetoothHeadset), and [BluetoothDevice](https://developer.android.com/reference/android/bluetooth/BluetoothDevice).

### API 37 boundary

The future claim is accurate and not backported. Current Android reference documentation marks public `BluetoothDevice.connect()` and `disconnect()` as added in API 37. Both are asynchronous, require `BLUETOOTH_CONNECT`, and additionally require `BLUETOOTH_PRIVILEGED` or an eligible `CompanionDeviceManager` association. The spike gates this route on SDK 37 plus association and describes it only as a future candidate requiring SDK, device, OEM, UX, and policy validation.

### Fail-closed decision model

The pure Kotlin model is conservative:

- no adapter returns unsupported;
- API 31+ without connect permission returns a permission plan;
- disabled Bluetooth opens settings;
- an unbonded system-profile selection is rejected;
- connecting/disconnecting suppresses a second action;
- generic unknown state remains state-only;
- hidden-profile strategy always falls back to settings;
- direct system-device action requires the explicit API 37 method, SDK 37+, companion association, and a determinate desired transition;
- direct GATT action is limited to the app-owned GATT profile/method pair.

The eight tests cover the main safety branches. The code is a decision artifact only: it contains no Android Bluetooth wrapper, Activity implementation, receiver/service logic, or runtime action path.

## Prohibited behavior check

Static inspection of the target found no app network permission/API, hidden API reflection, Shizuku, root/process execution, Accessibility service/automation, credentials, or secret handling. No full widget grid, selection UI, Android Bluetooth adapter, background service, or direct hidden/private toggle was added.

## Verification limitations and warnings

1. No Java/Kotlin/Gradle/Android SDK toolchain or Gradle wrapper is available in this environment. The Kotlin source and JUnit tests therefore could not be compiled or executed independently. This is acceptable for a feasibility spike, but not for the next implementation gate.
2. The conclusions rely on public documentation and static review. A2DP/headset aggregation, broadcasts, lifecycle behavior, settings intent behavior, permission UX, and OEM process management still require an instrumented build and Honor 200 Pro / MagicOS 8 testing.
3. API 37 is a future candidate, not part of API 26–35, Android 14, compileSdk 35, or the approved MVP. Companion association does not itself prove that every profile/device/OEM will behave acceptably.
4. A Settings fallback adds friction: widget tap is not a direct toggle. Product copy and UI must state that honestly.
5. The decision function accepts already-classified inputs. The future Android adapter must derive `availableMethod`, profile, association, bond, permission, and state from trusted platform checks rather than caller/UI claims.

These warnings do not block the spike's feasibility decision, but they materially constrain the product promise and define required evidence for implementation.

## Recommended next gate

Set up a reproducible Android toolchain/Gradle wrapper and CI first, then implement one narrowly scoped, interface-backed vertical slice: visible `ActionActivity`; API-aware permission handling; bonded inventory; A2DP/headset profile query; documented broadcast-as-invalidation handling; local selected-device storage; and system Bluetooth settings fallback. Require unit tests, Android lint/build, emulator or instrumented tests where applicable, and the documented manual matrix on Honor 200 Pro before widget-grid/selection UI or any direct-action experiment.

Keep hidden API reflection, Shizuku/root, Accessibility automation, app network calls, credentials, full widget UI, and API 37 direct toggling outside that gate.
