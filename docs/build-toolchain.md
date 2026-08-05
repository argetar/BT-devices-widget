# Android build toolchain

## Pinned project toolchain

| Component | Version |
|---|---|
| JDK | 17 (Temurin in CI) |
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin Android plugin | 2.0.21 |
| Android platform | API 35 |
| Android build tools | 35.0.0 |
| Minimum Android API | 26 |

Gradle 8.9 is the version required by Android Gradle Plugin 8.7. The CI workflow provisions it through the official Gradle action rather than committing an unverified `gradle-wrapper.jar` from a host without Gradle. All GitHub Actions are pinned to full commit SHAs, with their reviewed release tags recorded in comments.

## Local prerequisites

Install all of the following from trusted upstream packages:

1. A JDK 17 distribution and set `JAVA_HOME` to it.
2. Gradle 8.9 on `PATH`.
3. Android SDK command-line tools. Set `ANDROID_HOME` (or `ANDROID_SDK_ROOT`) and add its `cmdline-tools/latest/bin` and `platform-tools` directories to `PATH`.
4. Android SDK platform 35 and build tools 35.0.0:

```bash
sdkmanager 'platforms;android-35' 'build-tools;35.0.0'
```

Do not commit `local.properties`; it is host-specific and ignored by Git.

## Exact verification commands

From the repository root:

```bash
java -version
gradle --version
gradle --no-daemon --stacktrace assembleDebug
gradle --no-daemon --stacktrace testDebugUnitTest
gradle --no-daemon --stacktrace lintDebug
```

These are the same Gradle tasks used by CI. Reports are written below `app/build/reports/`, and the debug APK is written below `app/build/outputs/apk/debug/`.

## CI supply-chain notes

The workflow grants only `contents: read` and runs on `ubuntu-24.04`. It installs exact Gradle, Android platform, and build-tools versions. Action SHA provenance can be rechecked without executing their code:

```bash
git ls-remote https://github.com/actions/checkout.git 'refs/tags/v4.2.2*'
git ls-remote https://github.com/actions/setup-java.git 'refs/tags/v4.7.1*'
git ls-remote https://github.com/android-actions/setup-android.git 'refs/tags/v3.2.2*'
git ls-remote https://github.com/gradle/actions.git 'refs/tags/v4.3.1*'
```

The Gradle action downloads the official Gradle 8.9 distribution and performs its normal distribution verification. No wrapper JAR or other opaque executable was added in this gate. A future wrapper may be generated only from this validated Gradle 8.9 setup, followed by wrapper validation and a recorded JAR SHA-256.
