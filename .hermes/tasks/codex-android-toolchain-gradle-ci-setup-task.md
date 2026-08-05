# Codex task: android-toolchain-gradle-ci-setup

## Context

Project: **BT devices widget**

Repo:

`/home/eli/BT-devices-widget`

GitHub:

`https://github.com/argetar/BT-devices-widget.git`

Accepted previous gate:

- `bt-api-feasibility-spike`
- Verdict: `PASS_WITH_WARNINGS`
- Review: `/home/eli/BT-devices-widget/.hermes/reviews/crestodian-bt-api-feasibility-spike-review.md`
- Current state: `/home/eli/BT-devices-widget/.hermes/state.md`

Key constraint from review:

Local Hermes host lacks Java, Gradle, Kotlin compiler, Android SDK, and Gradle wrapper. The next implementation gates must have reproducible build/test/lint evidence before Bluetooth runtime/UI work continues.

## Goal

Create a reproducible Android build/test/lint path for the project using a safe Gradle wrapper and GitHub Actions CI, without implementing Bluetooth runtime behavior or widget UI features.

This gate is infrastructure-only.

## Required outcomes

1. The repository has a reproducible Gradle wrapper or an explicitly justified, reproducible CI Gradle setup.
2. GitHub Actions can build and test the Android project on Linux using JDK 17 and Android SDK components.
3. CI runs at minimum:
   - assemble debug APK;
   - JVM unit tests for the decision model;
   - Android lint or clearly documented lint blocker if initial scaffold needs a follow-up fix.
4. Project documentation explains local build prerequisites and commands.
5. No Bluetooth runtime, UI, hidden API, Shizuku/root, Accessibility, network, or credential behavior is introduced.

## Important supply-chain constraints

Gradle wrapper files are executable supply-chain artifacts. Do not add opaque binaries casually.

Preferred safe paths, in order:

1. If a trusted Gradle installation is available in the environment, generate wrapper with a pinned Gradle version and record commands/checks.
2. If Gradle is unavailable locally, add GitHub Actions using a pinned action/tooling path that provisions Gradle reproducibly, and document that wrapper generation remains blocked locally.
3. If adding `gradle-wrapper.jar`, document its source and checksum, and keep the change minimal for Crestodian review. Do not download arbitrary jars without provenance.

Use versions compatible with current scaffold:

- Android Gradle Plugin: current project uses `8.7.3`.
- Kotlin plugin: current project uses `2.0.21`.
- JDK: 17.
- compileSdk/targetSdk: 35.
- minSdk: 26.

Codex may propose version adjustments only if required to make the build reliable; document why.

## Suggested files

Likely create/modify:

- `.github/workflows/android.yml`
- `README.md`
- optionally `gradle/wrapper/gradle-wrapper.properties`
- optionally `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar` only with provenance/checksum
- optionally `docs/build-toolchain.md`
- `.hermes/results/codex-android-toolchain-gradle-ci-setup-result.md`

## Required checks

Run what is actually available. Do not fabricate unavailable build output.

Always run:

- `git diff --check`
- static secret scan for common credentials/tokens/private keys;
- static scan confirming no app network/hidden reflection/Shizuku/root/Accessibility/process behavior was added;
- remote URL token check;
- `git status --short --branch`.

If toolchain becomes available locally, also run:

- `./gradlew --version`
- `./gradlew assembleDebug`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug` or equivalent lint task.

If local toolchain remains unavailable, document exact blockers and ensure CI workflow is syntactically reviewable.

## Forbidden

Do not:

- implement Bluetooth runtime adapters;
- implement ActionActivity or widget grid;
- implement device selection UI;
- add hidden API reflection;
- add Shizuku/root/privileged behavior;
- add Accessibility automation;
- add app network calls;
- add credentials/secrets;
- push to git.

## Result file

Save full result to:

`/home/eli/BT-devices-widget/.hermes/results/codex-android-toolchain-gradle-ci-setup-result.md`

The result must include:

- summary;
- files changed;
- exact build/toolchain approach;
- Gradle/AGP/Kotlin/JDK versions;
- wrapper provenance/checksum if wrapper files are added;
- tests/checks actually run;
- build/lint blockers if any;
- commit SHA;
- non-secret/non-push confirmation;
- recommended next gate.

Commit locally if clean. Do not push.
