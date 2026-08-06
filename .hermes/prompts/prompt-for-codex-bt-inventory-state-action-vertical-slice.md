START NOW — bt-inventory-state-action-vertical-slice

Repo:
/home/eli/BT-devices-widget

Task file:
/home/eli/BT-devices-widget/.hermes/tasks/codex-bt-inventory-state-action-vertical-slice-task.md

Start immediately. Do not send a confirmation ACK or handshake. Execute the task file now, work autonomously until the result file is saved, and do not stop after a plan. Send progress only if work takes longer than 5 minutes or there is a concrete blocker; progress must name completed checks and the currently running step.

Scope summary:
- narrow interface-backed Android runtime vertical slice after feasibility + CI gates PASS_WITH_WARNINGS;
- permission-aware bonded inventory;
- public A2DP/headset state observation;
- broadcast invalidation as re-query trigger;
- local selected-device storage;
- visible ActionActivity for permission/Bluetooth disabled handling and Settings fallback;
- fail-closed JVM-testable logic.

Forbidden:
- no full widget grid UI;
- no polished device-selection UI;
- no direct system connect/disconnect experiment;
- no hidden API reflection runtime/default path;
- no Shizuku/root/privileged behavior;
- no Accessibility automation;
- no app network calls;
- no secrets/credentials;
- no Gradle wrapper JAR or action pin upgrades unless strictly required and justified;
- do not push.

Required checks:
- git diff --check;
- static secret scan;
- static scan for hidden reflection/Shizuku/root/Accessibility/app network/process behavior;
- remote URL token check;
- git status --short --branch;
- if toolchain available: assembleDebug, testDebugUnitTest, lintDebug;
- if local toolchain unavailable: document exact blocker so Hermes can verify CI after push.

Save full result to:
/home/eli/BT-devices-widget/.hermes/results/codex-bt-inventory-state-action-vertical-slice-result.md

Commit locally if clean. Telegram final: short summary, commit SHA, tests/checks, result path only.
