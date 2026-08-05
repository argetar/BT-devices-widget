# Result: android-ci-jvm-target-fix

## Summary

Applied the minimal Gradle configuration fix for the reported `:app:compileDebugKotlin` failure. Java source/target compatibility and the Kotlin JVM toolchain are now both explicitly set to Java 17, matching the JDK 17 CI environment. No workflow, dependency, Android runtime, Bluetooth, or UI behavior changed.

## Files changed

- `app/build.gradle.kts`: added Android `compileOptions` with Java 17 source/target compatibility and `kotlin.jvmToolchain(17)`.
- `.hermes/results/codex-android-ci-jvm-target-fix-result.md`: this report.

## Configuration fix

The prior failure reported `compileDebugJavaWithJavac` targeting 1.8 while `compileDebugKotlin` targeted 17. The application module now declares:

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

This makes the target contract explicit for both compilers while preserving the existing JDK 17 CI setup.

## Checks actually run

- `git diff --check`: passed.
- `git diff --cached --check` before commit: passed.
- Static secret scan for private-key headers and common GitHub, Google, AWS, password, secret, API-key, and access-token assignment patterns: passed, no matches.
- Static application scan for network APIs, hidden/reflection APIs, Shizuku, Accessibility service, root/process execution: passed, no matches.
- Remote URL: `https://github.com/argetar/BT-devices-widget.git`; embedded-token check passed.
- Final `git status --short --branch`: clean after the result commit, branch ahead of `origin/main`; nothing pushed.

## Local build blocker

The host still has no `java`, `gradle`, `kotlinc`, `sdkmanager`, or executable `./gradlew`. Therefore `assembleDebug`, `testDebugUnitTest`, and `lintDebug` could not be run locally. The next authorized GitHub Actions run must confirm the target-alignment fix and expose any subsequent build issue; no build outcome was fabricated.

## Commit

Configuration fix commit: `d4017f30cbe04a5389f68b4f9c357c7e952a146d`.

This tracked report is committed in a follow-up local commit so the repository ends clean. The report-containing SHA is supplied in the final handoff because a commit cannot contain its own SHA.

## Scope and safety confirmation

No workflow actions, wrapper JAR, dependencies, runtime adapters, UI, hidden APIs, Shizuku/root behavior, Accessibility automation, app network calls, processes, secrets, or credentials were added. Nothing was pushed.
