START NOW — bt-api-feasibility-spike

Repo:
/home/eli/BT-devices-widget

Task file:
/home/eli/BT-devices-widget/.hermes/tasks/codex-bt-api-feasibility-spike-task.md

Start immediately. Do not send a confirmation ACK or handshake. Execute the task file now, work autonomously until the result file is saved, and do not stop after a plan. Send progress only if work takes longer than 5 minutes or there is a concrete blocker; progress must name completed checks and the currently running step.

Scope summary:
- technical feasibility spike for Android Bluetooth device discovery, connection state, and connect/disconnect behavior;
- primary MVP target Honor 200 Pro / Android 14, but design should be broader and future Play Store compatible;
- reference apps: Bluetooth Audio Connect Widget, Bluetooth AudioWidget+;
- first MVP remains selected devices + state + tap connect/disconnect, not advanced audio features yet;
- produce docs + small testable capability/action decision foundation, not full UI.

Forbidden:
- no full widget UI implementation;
- no final device-selection UI;
- no hidden API reflection as default path;
- no Shizuku/root/privileged behavior;
- no Accessibility automation;
- no app network calls;
- no secrets/credentials;
- do not push.

Required checks:
- Gradle/Android build or document blocker if Java/Gradle/Android SDK unavailable;
- unit tests if toolchain available;
- git diff --check;
- static scan for secrets/credentials;
- git status --short --branch;
- verify remote URL contains no token.

Save full result to:
/home/eli/BT-devices-widget/.hermes/results/codex-bt-api-feasibility-spike-result.md

Commit locally if clean. Telegram final: short summary, commit SHA, tests/checks, result path only.
