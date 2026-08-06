# BT devices widget — state

## Current accepted gate

- Gate: `android-toolchain-gradle-ci-setup`
- Verdict: `PASS_WITH_WARNINGS`
- Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-android-toolchain-gradle-ci-setup-review.md`
- Reviewed commit: `4b139ff181088e5a0e3caa77cfcd834b382ece0a`
- Successful CI run: `https://github.com/argetar/BT-devices-widget/actions/runs/31050602768`

## Previous accepted gate

- Gate: `bt-api-feasibility-spike`
- Verdict: `PASS_WITH_WARNINGS`
- Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-bt-api-feasibility-spike-review.md`
- Reviewed commit: `bf975b92cb036c3241effde37c0eba949e21731b`

## Decision summary

Bluetooth feasibility accepted with warnings:

- API 26–35 / Android 14 supports paired/bonded inventory and public A2DP/headset state observation after permission.
- API 26–35 does not provide a reliable public SDK system-level connect/disconnect toggle for A2DP/headset/HID/generic bonded devices.
- Android 14 MVP must be fail-closed: visible Activity for permission/Bluetooth disabled handling and Bluetooth Settings fallback for unsupported direct toggles.
- Direct `BluetoothDevice.connect()` / `disconnect()` is only a future API 37 candidate requiring `BLUETOOTH_CONNECT` plus privileged permission or companion association.

Android CI/toolchain accepted with warnings:

- CI successfully ran `assembleDebug`, 8 JVM tests, and `lintDebug` for commit `4b139ff...`.
- Workflow permissions are read-only and action references are pinned to full SHA with verified tag provenance.
- Java/Kotlin target is consistently 17.
- Warnings: not bit-for-bit hermetic (`ubuntu-24.04`, Temurin patch, Android command-line tooling can move); no Gradle wrapper yet; GitHub reports Node.js 20 deprecation for current pinned action revisions.

## Active follow-ups

Parallel infrastructure follow-up, not blocking the next implementation gate:

- Gradle wrapper generation/provenance once trusted toolchain path is selected.
- Node.js-24-native GitHub Actions pin upgrades with full-SHA provenance.

## Next recommended gate

`bt-inventory-state-action-vertical-slice`

Scope:

- visible `ActionActivity`;
- permission-aware bonded inventory;
- public A2DP/headset state query;
- documented broadcast invalidation;
- local selected-device storage;
- Settings fallback;
- fail-closed interfaces + JVM tests;
- green assemble/test/lint CI;
- manual permission/state/lifecycle matrix on Honor 200 Pro before widget-grid UI.

Out of scope:

- full widget grid UI;
- direct-action experiment;
- hidden API reflection;
- Shizuku/root/privileged behavior;
- Accessibility automation;
- app network calls;
- credentials/secrets.
