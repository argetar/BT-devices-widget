# Result: android-toolchain-gradle-ci-setup

## Summary

Added an infrastructure-only, reproducible Android CI path. GitHub Actions now provisions JDK 17, exact Gradle 8.9, Android platform 35, and build tools 35.0.0, then runs toolchain reporting, `assembleDebug`, `testDebugUnitTest`, and `lintDebug`. The workflow has read-only repository permissions, a fixed Ubuntu runner image, a timeout, concurrency cancellation, and every third-party action is pinned to a full commit SHA.

No Gradle wrapper binary was added because this host cannot generate or validate one from an installed Gradle distribution. This follows the task's second preferred supply-chain path and avoids committing an opaque downloaded JAR.

## Files changed

- `.github/workflows/android.yml`: pinned Linux build/test/lint workflow.
- `docs/build-toolchain.md`: exact local prerequisites, commands, versions, action provenance checks, and wrapper policy.
- `README.md`: concise build/test entry point linking to the toolchain document.
- `.hermes/results/codex-android-toolchain-gradle-ci-setup-result.md`: this result.

No application source, manifest, Bluetooth behavior, widget behavior, or UI was changed.

## Build/toolchain approach

CI uses `ubuntu-24.04`, Temurin JDK 17, the official Android setup action, explicitly installed `platforms;android-35` and `build-tools;35.0.0`, and the official Gradle setup action with `gradle-version: '8.9'`. It invokes the installed `gradle` command directly because the repository has no safely generated wrapper.

The actions are immutable SHA pins with release tags documented inline:

- `actions/checkout` v4.2.2: `11bd71901bbe5b1630ceea73d27597364c9af683`
- `actions/setup-java` v4.7.1: `c5195efecf7bdfc987ee8bae7a71cb8b11521c00`
- `android-actions/setup-android` v3.2.2: `9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407`
- `gradle/actions/setup-gradle` v4.3.1 peeled commit: `06832c7b30a0129d7fb559bcc6e43d26f6374244`

The tag-to-commit mappings were checked using `git ls-remote` against the four upstream GitHub repositories. For Gradle's annotated v4.3.1 tag, the workflow pins the peeled commit (`^{}`).

## Versions

| Component | Version |
|---|---|
| JDK | 17, Temurin |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin Android plugin | 2.0.21 |
| compileSdk / targetSdk | 35 / 35 |
| Android build tools | 35.0.0 |
| minSdk | 26 |

## Wrapper provenance/checksum, if applicable

Not applicable: no `gradlew`, `gradlew.bat`, `gradle-wrapper.jar`, or wrapper properties were added. Local wrapper generation is blocked by absent Java and Gradle. The documented future procedure is to generate a Gradle 8.9 wrapper only from the validated CI/toolchain path, validate it, and record the wrapper JAR SHA-256 before committing it.

## Tests/checks actually run

- `git diff --cached --check` before infrastructure commit: passed.
- `git diff --check`: passed.
- Static secret scan for private-key headers and common GitHub, Google, AWS, password, secret, API-key, and access-token assignment patterns: passed, no matches.
- Static app scan for network APIs, hidden/reflection APIs, Shizuku, Accessibility service, root/process execution: passed, no matches.
- Remote URL inspection: `https://github.com/argetar/BT-devices-widget.git`; no embedded token pattern.
- Action release provenance: all four configured action SHA pins matched their documented upstream tags; passed.
- Workflow was reviewed as text; no local `actionlint` or PyYAML parser was available.
- Final `git status --short --branch`: clean after report commit, branch ahead of `origin/main`; no push performed.

The CI workflow is configured to run, in separate observable steps:

- `gradle --version`
- `gradle --no-daemon --stacktrace assembleDebug`
- `gradle --no-daemon --stacktrace testDebugUnitTest`
- `gradle --no-daemon --stacktrace lintDebug`

## Build/lint blockers, if any

This host has no `java`, `gradle`, `kotlinc`, `sdkmanager`, executable `./gradlew`, `ANDROID_HOME`, or `ANDROID_SDK_ROOT`. Therefore local Gradle version, assembly, JVM unit tests, and lint could not be run. The workflow itself cannot be executed without a push or external GitHub Actions dispatch, both outside this task; no CI outcome was fabricated.

## Commit

Infrastructure commit: `f8cd008e3871d20afea4cc79c5defec24190be11`.

This tracked result is committed in a follow-up local commit so the repository ends clean. The report-containing commit is supplied in the final handoff because a commit cannot contain its own SHA.

## Non-secret/non-push confirmation

No secrets or credentials were read or added. No application network calls, Bluetooth runtime adapter, ActionActivity, widget grid, device-selection UI, hidden reflection, Shizuku/root behavior, Accessibility automation, or process execution was added. Nothing was pushed and no external repository state was changed.

## Recommended next gate

Push or otherwise run the pinned workflow through an authorized CI path and capture successful `assembleDebug`, decision-model unit-test, and `lintDebug` evidence. If all three pass, generate and validate a Gradle 8.9 wrapper within that trusted toolchain, recording the wrapper JAR SHA-256. Only after reproducible build evidence should the interface-backed Bluetooth inventory/state adapter gate begin.
