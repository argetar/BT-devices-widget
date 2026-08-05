# Crestodian review brief: Android toolchain / Gradle CI setup

## Review target

Repository:

`/home/eli/BT-devices-widget`

GitHub:

`https://github.com/argetar/BT-devices-widget.git`

Reviewed commits:

- `f8cd008e3871d20afea4cc79c5defec24190be11` — Add reproducible Android CI toolchain
- `6734784be2ef58c05898af5247e92c3138f85d7a` — Record Android toolchain CI setup result
- `d4017f30cbe04a5389f68b4f9c357c7e952a146d` — Align Android Java and Kotlin JVM targets
- `4b139ff181088e5a0e3caa77cfcd834b382ece0a` — Record Android JVM target fix result

Current HEAD at handoff creation:

`4b139ff181088e5a0e3caa77cfcd834b382ece0a`

Relevant result files:

- `/home/eli/BT-devices-widget/.hermes/results/codex-android-toolchain-gradle-ci-setup-result.md`
- `/home/eli/BT-devices-widget/.hermes/results/codex-android-ci-jvm-target-fix-result.md`

## Gate purpose

This gate is infrastructure-only after `bt-api-feasibility-spike` `PASS_WITH_WARNINGS`.

Purpose:

- establish reproducible Android build/test/lint evidence;
- avoid adding unverified Gradle wrapper JAR;
- use pinned GitHub Actions and exact toolchain versions;
- fix only minimal Gradle JVM target config needed to make CI green;
- do not implement Bluetooth runtime, UI, direct-toggle experiments, or hidden/private behavior.

## Changed files in scope

```text
.github/workflows/android.yml
README.md
docs/build-toolchain.md
app/build.gradle.kts
.hermes/results/codex-android-toolchain-gradle-ci-setup-result.md
.hermes/results/codex-android-ci-jvm-target-fix-result.md
```

No application source under `app/src/main/...` should have changed in this gate.

## Toolchain/CI design

Workflow:

`/.github/workflows/android.yml`

Key properties:

- `permissions: contents: read`
- `runs-on: ubuntu-24.04`
- timeout 30 minutes
- concurrency cancellation per ref
- JDK 17 Temurin
- Android SDK platform 35 + build-tools 35.0.0
- Gradle 8.9 via Gradle action
- no Gradle wrapper JAR added
- steps:
  - `gradle --version`
  - `gradle --no-daemon --stacktrace assembleDebug`
  - `gradle --no-daemon --stacktrace testDebugUnitTest`
  - `gradle --no-daemon --stacktrace lintDebug`

Pinned actions:

```text
actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683 # v4.2.2
actions/setup-java@c5195efecf7bdfc987ee8bae7a71cb8b11521c00 # v4.7.1
android-actions/setup-android@9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407 # v3.2.2
gradle/actions/setup-gradle@06832c7b30a0129d7fb559bcc6e43d26f6374244 # v4.3.1 peeled commit
```

Hermes independently verified each SHA against its upstream tag via `git ls-remote`; Gradle action uses the peeled commit for annotated tag `v4.3.1`.

## CI evidence

Initial CI run after `6734784...` failed in `assembleDebug` because Java/Kotlin JVM targets were inconsistent:

```text
compileDebugJavaWithJavac: 1.8
compileDebugKotlin: 17
```

Codex fixed this minimally in `app/build.gradle.kts`:

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}
```

Successful CI run after `4b139ff...`:

- Run: `https://github.com/argetar/BT-devices-widget/actions/runs/31050602768`
- Job: `https://github.com/argetar/BT-devices-widget/actions/runs/31050602768/job/92456616307`
- Status: completed
- Conclusion: success

Successful steps:

```text
Check out repository
Set up JDK 17
Set up Android SDK
Install pinned Android SDK components
Set up Gradle 8.9
Report toolchain
Assemble debug APK
Run JVM unit tests
Run Android lint
```

## Hermes verification performed

```text
git diff --check origin/main..HEAD before push: OK
secret scan: 0 findings
app capability scan for network/reflection/Shizuku/Accessibility/root/process execution: 0 actionable findings
remote URL token check: OK
local toolchain still missing: java/gradle/kotlinc/sdkmanager/wrapper absent
push after workflow-scope token update: OK, remote_matches_local=yes
CI run for 4b139ff: success
```

## Review questions

Please decide `PASS`, `PASS_WITH_WARNINGS`, or `BLOCKED`.

Focus on:

1. Is the no-wrapper-JAR CI approach acceptable for this gate?
2. Are pinned action SHAs and documented provenance sufficient?
3. Is the Gradle/JDK/AGP/Kotlin configuration coherent and reproducible?
4. Is the Java/Kotlin target fix minimal and correct?
5. Does the workflow have appropriately restricted permissions and no credential/secrets exposure?
6. Are build, unit-test, and lint evidence sufficient to unblock the next implementation gate?
7. What should be the next gate: interface-backed Bluetooth inventory/state/action vertical slice, or wrapper generation/provenance first?

## Expected output

Save full review to:

`/home/eli/BT-devices-widget/.hermes/reviews/crestodian-android-toolchain-gradle-ci-setup-review.md`

Telegram final should include only:

- verdict;
- review file path;
- blocking issues, if any;
- warning impact;
- recommended next gate.
