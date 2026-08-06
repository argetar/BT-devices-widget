# Crestodian review: Android toolchain / Gradle CI setup

## Verdict

**PASS_WITH_WARNINGS**

The infrastructure gate is sufficient to unblock the next narrow implementation gate. Commit `4b139ff181088e5a0e3caa77cfcd834b382ece0a` has successful, externally verifiable assemble, JVM-test, and lint evidence; the Java/Kotlin target correction is minimal and coherent; workflow permissions are restricted; action references have valid immutable SHA provenance; and no out-of-scope application capability was introduced.

The warnings concern the strength and longevity of reproducibility, not the correctness of this gate: several provisioned toolchain layers are version families or mutable hosted images rather than fully immutable inputs, GitHub reports that the pinned action revisions target deprecated Node.js 20, and the repository still has no validated Gradle wrapper.

## Blocking issues

None for this infrastructure-only gate.

## Findings

### 1. CI result and task evidence — pass

GitHub Actions run `31050602768` is publicly attributable to head SHA `4b139ff181088e5a0e3caa77cfcd834b382ece0a`, status `completed`, conclusion `success`, on the `main` branch. Job `92456616307` (`build-test-lint`) completed successfully on `ubuntu-24.04`.

The GitHub jobs API records success independently for:

- checkout;
- JDK 17 setup;
- Android SDK setup and component installation;
- Gradle 8.9 setup and toolchain reporting;
- `assembleDebug`;
- `testDebugUnitTest`;
- `lintDebug`.

The repository contains eight focused JVM test methods for the existing fail-closed decision model, so the unit-test step is substantive rather than an empty/no-source task. The run page reports one infrastructure annotation only (Node.js 20 deprecation), not a build, test, or lint failure.

### 2. Workflow permissions and execution bounds — pass

`.github/workflows/android.yml` declares workflow-level `permissions: contents: read`. No write permission, secret reference, credential input, deployment, artifact publication, package publication, or external application call is configured. The job has a 30-minute timeout and per-ref concurrency cancellation.

The workflow triggers for pull requests and pushes to `main`. Its commands are fixed build commands with no interpolation from pull-request titles, commit messages, or other attacker-controlled text.

### 3. Action SHA provenance — pass

Independent `git ls-remote` verification matched every configured full SHA to the documented upstream release tag:

- `actions/checkout@11bd71901bbe5b1630ceea73d27597364c9af683` = `v4.2.2`;
- `actions/setup-java@c5195efecf7bdfc987ee8bae7a71cb8b11521c00` = `v4.7.1`;
- `android-actions/setup-android@9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407` = `v3.2.2`;
- `gradle/actions/setup-gradle@06832c7b30a0129d7fb559bcc6e43d26f6374244` = the peeled commit for annotated tag `v4.3.1` (tag object `b5c8a0d...`).

Using the peeled commit for the annotated Gradle tag is correct. Inline tag comments plus the documented verification commands are sufficient provenance for this gate.

### 4. Gradle/JDK/AGP/Kotlin coherence — pass with reproducibility warning

The declared combination is coherent and has passed CI:

- Gradle 8.9;
- Android Gradle Plugin 8.7.3;
- Kotlin Android plugin 2.0.21;
- JDK/JVM target 17;
- compile/target SDK 35;
- Android build tools 35.0.0;
- minimum SDK 26.

The fix in `d4017f3` adds only Java 17 `sourceCompatibility`/`targetCompatibility` and Kotlin `jvmToolchain(17)`. It directly resolves the reported Java 1.8 versus Kotlin 17 mismatch without changing dependencies, runtime code, or workflow behavior.

The documentation overstates the word “exact” for the complete environment. `ubuntu-24.04` is a moving hosted-runner image; `java-version: '17'` selects the current available Temurin 17 patch; and `setup-android` provisions Android command-line tools without an immutable revision pin. Gradle, AGP, Kotlin plugin, platform, and build-tools versions are explicitly fixed, but bit-for-bit environment reproduction is not established. This does not invalidate the green gate, but future investigations should preserve the run's reported tool versions and consider stronger pins where practical.

### 5. No-wrapper approach — acceptable with warning

No `gradlew`, `gradlew.bat`, `gradle-wrapper.properties`, or `gradle-wrapper.jar` exists. Avoiding an opaque wrapper JAR that the available host could neither generate nor validate was a defensible supply-chain choice. The official, SHA-pinned Gradle action installs Gradle 8.9 and its resulting path has now passed CI.

The tradeoff is that local commands require a separately installed exact Gradle and do not yet provide the standard repository-owned bootstrap experience. Wrapper generation and validation remains worthwhile, but it is not a prerequisite for starting the next interface-backed vertical slice because the authorized CI path is already green.

### 6. Node runtime lifecycle — warning

GitHub annotated the successful run because all four pinned action revisions target Node.js 20 and are being forced to execute on Node.js 24. The run still passed, so this is not blocking. However, the current pins should be deliberately upgraded to reviewed, full-SHA action releases with native Node.js 24 support when those versions are selected and provenance-checked; otherwise a future hosted-runner enforcement change could break CI despite unchanged project code.

### 7. Scope and prohibited behavior — pass

The scoped change set from the preceding reviewed gate through `4b139ff` contains only:

- `.github/workflows/android.yml`;
- build documentation and README changes;
- the Java/Kotlin target configuration;
- Codex result records.

There is no change under `app/src`. Static inspection found no newly added Bluetooth runtime or UI behavior, hidden API reflection, Shizuku/root integration, Accessibility automation, application network call, process execution, secret, or credential behavior. `git diff --check e4c7bbb..4b139ff` passed.

## Warning impact

The successful run is valid evidence that this commit assembles, its existing decision-model JVM tests pass, and Android lint passes. The warnings mean this should be described as a reproducible CI recipe at the declared major/component level, not yet as a bit-for-bit hermetic toolchain. The Node.js annotation also creates maintenance work for the action pins. Neither issue permits or conceals runtime Bluetooth behavior and neither blocks the next narrow implementation gate.

## Evidence reviewed

- target commit and history through `4b139ff181088e5a0e3caa77cfcd834b382ece0a`;
- review brief and both Codex result files;
- complete scoped diff and `git diff --check`;
- workflow, root/module Gradle configuration, build-toolchain documentation, and test sources;
- absence of wrapper files and absence of scoped `app/src` changes;
- upstream action tag refs via `git ls-remote`;
- GitHub Actions run `31050602768` and job `92456616307` through the public GitHub page/API;
- focused static scan for prohibited capabilities and credential patterns.

Local Android build execution was not repeated because this host still exposes no Java, Gradle, or `sdkmanager`; the successful authoritative CI run supplies the missing build/test/lint evidence.

## Recommended next gate

Proceed with an **interface-backed Bluetooth inventory/state/action vertical slice**: permission-aware bonded-device inventory, public A2DP/headset state observation, broadcast invalidation, local selection, visible `ActionActivity`, and Bluetooth Settings fallback, with fail-closed interfaces and JVM tests. Require the existing assemble/test/lint CI on that slice plus a manual permission/state/lifecycle matrix on the Honor 200 Pro before expanding widget UI or attempting any direct-action experiment.

Track wrapper generation/provenance and Node.js-24-native action-pin upgrades as a small parallel infrastructure follow-up, not as a blocker for that vertical slice.
