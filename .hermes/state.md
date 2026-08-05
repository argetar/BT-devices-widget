# BT devices widget — state

## Current accepted gate

- Gate: `bt-api-feasibility-spike`
- Verdict: `PASS_WITH_WARNINGS`
- Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-bt-api-feasibility-spike-review.md`
- Reviewed commit: `bf975b92cb036c3241effde37c0eba949e21731b`
- Handoff commit: `0c3e5d80c88529916c086bfc43b64c48746f315f`

## Decision summary

The feasibility conclusion is accepted with warnings:

- API 26–35 / Android 14 supports paired/bonded inventory and public A2DP/headset state observation after permission.
- API 26–35 does not provide a reliable public SDK system-level connect/disconnect toggle for A2DP/headset/HID/generic bonded devices.
- Android 14 MVP must be fail-closed: visible Activity for permission/Bluetooth disabled handling and Bluetooth Settings fallback for unsupported direct toggles.
- Direct `BluetoothDevice.connect()` / `disconnect()` is only a future API 37 candidate requiring `BLUETOOTH_CONNECT` plus privileged permission or companion association.
- No hidden API reflection, Shizuku/root, Accessibility automation, app network calls, credentials, or full widget UI were added.

## Active blocker

Local Hermes host has no Android build toolchain:

- Java missing
- Gradle missing
- Kotlin compiler missing
- Android SDK missing
- Gradle wrapper missing

## Next recommended gate

`android-toolchain-gradle-ci-setup`

Goal:

- make Android build/test/lint reproducible via Gradle wrapper and/or CI;
- do not implement Bluetooth runtime/UI yet;
- enable future gates to prove Kotlin/JUnit/Android build correctness.

After that:

`bt-inventory-state-action-vertical-slice`

Scope:

- visible `ActionActivity`;
- permission-aware bonded inventory;
- A2DP/headset state query;
- documented broadcast invalidation;
- local selected-device storage;
- Settings fallback;
- manual matrix on Honor 200 Pro before widget-grid UI.
