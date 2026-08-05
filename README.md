# BT devices widget

Android home-screen widget for selected Bluetooth devices.

Planned goal:

- show selected Bluetooth devices in a compact widget grid, up to three icon columns;
- show connected devices in green;
- tap a device to connect or disconnect according to current state where Android permissions/APIs allow it;
- allow widget visual customization, especially background transparency.

> Status: initial project scaffold + implementation plan. Core Bluetooth behavior is intentionally not implemented yet pending API feasibility confirmation and plan approval.

## Build and test

The reproducible CI path uses JDK 17, Gradle 8.9, Android platform 35, and build tools 35.0.0. It assembles the debug APK, runs JVM unit tests, and runs Android lint on every pull request and main-branch push.

See [docs/build-toolchain.md](docs/build-toolchain.md) for local prerequisites, exact commands, version pins, and supply-chain provenance.
