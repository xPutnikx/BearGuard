# BearGuard

A privacy-focused firewall app for Android built with Kotlin Multiplatform and Compose Multiplatform.

BearGuard gives you complete control over which apps can access the internet, helping you protect your privacy and save battery.

## Features

- **Per-App Firewall** - Block or allow internet access for individual apps
- **Network Type Rules** - Set different rules for WiFi vs Mobile data
- **Screen Off Rules** - Block apps when screen is off to save battery
- **Lockdown Mode** - Block all apps except those explicitly allowed
- **Traffic Monitoring** - View real-time network connections
- **New App Detection** - Get notified when new apps are installed
- **Auto-Start on Boot** - Automatically start protection when device boots
- **Theme Support** - Light, Dark, or System theme

## How It Works

BearGuard uses Android's VpnService API to create a local VPN tunnel. Unlike traditional VPNs, no data leaves your device - it works entirely locally as a "sinkhole" firewall:

1. Blocked apps have their traffic routed through the VPN tunnel where packets are dropped
2. Allowed apps bypass the VPN and connect normally
3. No external servers, no data collection, complete privacy

## Project Structure

```
BearGuard/
├── composeApp/                    # Main application module
│   └── src/
│       ├── commonMain/            # Shared Kotlin code
│       │   └── kotlin/
│       │       └── com/bearminds/bearguard/
│       │           ├── rules/     # App rules & blocking logic
│       │           ├── settings/  # Settings management
│       │           ├── traffic/   # Traffic monitoring
│       │           ├── network/   # Network type detection
│       │           ├── screen/    # Screen state detection
│       │           └── di/        # Dependency injection
│       ├── androidMain/           # Android-specific code
│       │   └── kotlin/
│       │       └── com/bearminds/bearguard/
│       │           ├── vpn/       # VPN service implementation
│       │           └── receiver/  # Broadcast receivers
│       ├── iosMain/               # iOS stubs (future)
│       ├── jvmMain/               # Desktop stubs (future)
│       └── commonTest/            # Unit tests
├── architecture/                  # Base architecture (MVI)
├── theme/                         # Compose theme
├── docs/                          # Documentation
│   ├── IMPLEMENTATION_PLAN.md     # Development roadmap
│   └── TEST_PLAN.md               # Manual test checklist
└── gradle/                        # Gradle configuration
```

## Tech Stack

- **Kotlin Multiplatform** - Share code across platforms
- **Compose Multiplatform** - Modern declarative UI
- **Koin** - Dependency injection
- **DataStore** - Preferences persistence
- **Coroutines & Flow** - Asynchronous programming
- **MVI Architecture** - Unidirectional data flow
- **Mokkery** - Mocking for tests

## Building

### Prerequisites

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK 24+ (target SDK 36)

### Build Commands

```bash
# Build debug APK
./gradlew composeApp:assembleDebug

# Run unit tests
./gradlew composeApp:jvmTest

# Build release APK
./gradlew composeApp:assembleRelease
```

### Running

1. Open project in Android Studio
2. Select `composeApp` configuration
3. Run on device or emulator (API 24+)

## Permissions

| Permission | Purpose |
|------------|---------|
| `INTERNET` | Required for VPN tunnel to function |
| `FOREGROUND_SERVICE` | Keep VPN running in background |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Android 14+ foreground service requirement |
| `QUERY_ALL_PACKAGES` | Android 11+ requirement to list installed apps |
| `POST_NOTIFICATIONS` | Show notifications for new apps and VPN status |
| `RECEIVE_BOOT_COMPLETED` | Auto-start VPN when device boots (optional) |

**Note:** The app also requires VPN permission which is granted through a system dialog when starting protection.

## Architecture

BearGuard follows MVI (Model-View-Intent) architecture:

```
┌─────────────┐    Events    ┌─────────────┐    State    ┌─────────────┐
│    View     │ ──────────▶  │  ViewModel  │ ──────────▶ │    View     │
│  (Compose)  │              │   (MVI)     │             │  (Compose)  │
└─────────────┘              └─────────────┘             └─────────────┘
                                    │
                                    ▼
                             ┌─────────────┐
                             │ Repository  │
                             │ (DataStore) │
                             └─────────────┘
```

## Testing

Unit tests are located in `composeApp/src/commonTest/`:

- `HomeViewModelTest` - VPN control tests
- `AppListViewModelTest` - App rules tests
- `TrafficViewModelTest` - Traffic monitoring tests
- `SettingsViewModelTest` - Settings tests
- `RulesRepositoryImplTest` - Rules persistence tests

Run tests:
```bash
./gradlew composeApp:jvmTest
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Kotlin coding conventions
- Use meaningful commit messages
- Add tests for new features
- Update documentation as needed

## Roadmap

See [IMPLEMENTATION_PLAN.md](docs/IMPLEMENTATION_PLAN.md) for the full development roadmap.

**Upcoming features:**
- iOS support via Network Extension
- Domain/host blocking
- Traffic statistics per app
- Export/import rules
- PCAP export for debugging

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [NetGuard](https://github.com/M66B/NetGuard) - Inspiration and reference implementation
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - UI framework
- [Koin](https://insert-koin.io/) - Dependency injection

---

**Privacy Notice:** BearGuard operates entirely on your device. No data is collected, transmitted, or stored externally. Your firewall rules and traffic logs stay on your device.
