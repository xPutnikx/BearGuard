# BearGuard Implementation Plan

A Kotlin Multiplatform firewall app for Android and iOS.

## Current Status

### Completed Features

#### Core Infrastructure
- [x] KMP project structure (Android, iOS, JVM targets)
- [x] Koin dependency injection setup
- [x] MVI architecture with BaseViewModel
- [x] Navigation with bottom nav bar (Home, Apps, Traffic, Settings)
- [x] DataStore for preferences persistence
- [x] Unit tests for ViewModels and Repository (46 tests)

#### VPN Service (Android)
- [x] VpnService implementation with foreground notification
- [x] Start/Stop VPN toggle on Home screen
- [x] Loading state with 3-second timeout
- [x] Error snackbar when connection fails
- [x] `QUERY_ALL_PACKAGES` permission for Android 11+
- [x] App-level blocking via `addAllowedApplication()` sinkhole approach
- [x] Rule change observation with VPN restart

#### App Rules
- [x] App list with per-app internet toggle
- [x] System app detection (FLAG_SYSTEM | FLAG_UPDATED_SYSTEM_APP)
- [x] Internet permission detection
- [x] Enabled/disabled app detection
- [x] Search filter
- [x] "Show system apps" filter
- [x] "Show blocked only" filter
- [x] Rules persistence via DataStore

#### Traffic Monitoring
- [x] Traffic screen UI
- [x] Connection model (source IP, dest IP, protocol, bytes, timestamp)
- [x] Traffic repository interface
- [x] Basic packet reading in VPN service

#### Testing
- [x] Manual QA test plan (docs/TEST_PLAN.md)
- [x] HomeViewModelTest (11 tests)
- [x] AppListViewModelTest (14 tests)
- [x] TrafficViewModelTest (12 tests)
- [x] RulesRepositoryImplTest (9 tests)
- [x] SettingsViewModelTest (15 tests)

---

## Phase 0: Critical Bug Fixes (URGENT)

These issues were identified during architecture review and should be fixed before any new features.

### 0.1 Self-Exclusion Bug (HIGH PRIORITY)
**Issue:** BearGuard only excludes itself from VPN when there are no blocked apps.
When blocked apps exist, BearGuard is NOT excluded, meaning it could lose network access.

**Fix:**
```kotlin
// Always exclude BearGuard from VPN, regardless of blocked apps
try {
    builder.addDisallowedApplication(packageName)
} catch (e: Exception) {
    Log.w(TAG, "Failed to exclude self from VPN", e)
}
```

**Tasks:**
- [x] Always call `addDisallowedApplication(packageName)` for BearGuard itself
- [x] Add validation to prevent rules from blocking BearGuard's package name

### 0.2 Thread Blocking in Coroutine (HIGH PRIORITY)
**Issue:** `restartVpn()` uses `Thread.sleep(100)` which blocks the IO dispatcher thread.

**Fix:** Replace with `delay(100)` and make the function suspend.

**Tasks:**
- [x] Change `restartVpn()` to a suspend function
- [x] Replace `Thread.sleep(100)` with `delay(100)`

### 0.3 Rule Change Race Condition (MEDIUM PRIORITY)
**Issue:** Rapid rule changes can trigger multiple concurrent VPN restarts.

**Fix:** Add debounce to rule observation:
```kotlin
rulesRepository.observeRules()
    .debounce(300) // Wait for rapid changes to settle
    .collect { ... }
```

**Tasks:**
- [x] Add 300ms debounce to rule change observation
- [x] Cancel any pending restart before starting a new one

### 0.4 VPN State Polling Inefficiency (MEDIUM PRIORITY)
**Issue:** `AndroidVpnController` polls `BearGuardVpnService.isRunning` every second in a forever loop.

**Fix:** Replace the static Boolean with a StateFlow and observe it directly.

**Tasks:**
- [x] Change `BearGuardVpnService.isRunning` from `Boolean` to `MutableStateFlow<Boolean>`
- [x] Have `AndroidVpnController` observe the StateFlow instead of polling
- [x] Add proper lifecycle cleanup for the controller (no longer needed - no coroutine scope)

---

## Phase 1: Core Firewall Functionality

### 1.1 VPN Blocking Improvements
The basic blocking works but needs hardening.

**Tasks:**
- [x] ~~Inject RulesRepository into VpnService~~ (completed)
- [x] ~~On VPN start, get list of blocked packages~~ (completed)
- [x] ~~Use `addAllowedApplication()` for blocked apps (sinkhole)~~ (completed)
- [x] ~~Listen for rule changes and restart VPN to apply~~ (completed)
- [ ] Add error channel to VpnController for UI error reporting
- [ ] Detect VPN permission revocation and notify user
- [ ] Handle "always-on VPN" system setting gracefully

### 1.2 WiFi vs Mobile Data Rules
Allow different rules per network type.

**Note:** The `Rule` model already has `allowWifi` and `allowMobileData` fields but they are not used.

**Tasks:**
- [x] ~~Extend Rule model~~ (already has `allowWifi`, `allowMobileData`)
- [x] Create `NetworkTypeProvider` interface (expect/actual for KMP)
- [x] Implement Android `ConnectivityManager` wrapper
- [x] Register for network change broadcasts
- [x] Apply appropriate rules based on network type
- [x] Update UI to show WiFi/Mobile toggles per app
- [x] Restart VPN when network type changes

### 1.3 Screen On/Off Rules
Block when screen is off for battery savings.

**Tasks:**
- [ ] Add `allowWhenScreenOn` setting per app
- [ ] Register BroadcastReceiver for ACTION_SCREEN_ON/OFF
- [ ] Update VPN rules when screen state changes

### 1.4 Uninstalled App Cleanup
Clean up rules when apps are uninstalled.

**Tasks:**
- [x] Register BroadcastReceiver for PACKAGE_REMOVED - `PackageChangeReceiver.kt`
- [x] Remove rule for uninstalled package
- [x] VPN automatically restarts on rule changes (already implemented)

---

## Phase 2: Traffic Logging & Analysis 📊

### 2.1 Real Traffic Logging
Parse actual packets and log connections.

**Approach:**
- Parse IP headers to extract source/dest IP and port
- Use `ConnectivityManager.getConnectionOwnerUid()` to map to app
- Store in database

**Tasks:**
- [x] Implement IP header parsing (IPv4/IPv6) - `IpPacketParser.kt`
- [x] Extract protocol (TCP/UDP), ports, addresses
- [x] Map connections to apps via UID - `ConnectionOwnerResolver.kt`
- [x] Store connections in repository (in-memory for now, SQLDelight later)
- [x] Show real-time traffic in Traffic screen

### 2.2 Traffic Statistics
- [ ] Track bytes sent/received per app
- [ ] Track connection count per app
- [ ] Show usage in app list (e.g., "12.5 MB today")
- [ ] Daily/weekly/monthly stats

### 2.3 Connection Details
- [ ] Tap on connection to see details
- [ ] Resolve IP to hostname (reverse DNS)
- [ ] Show country/ASN info (optional, requires GeoIP database)

---

## Phase 3: Advanced Blocking 🛡️

### 3.1 Domain/Host Blocking
Block specific domains.

**Tasks:**
- [ ] Add domain-based rules
- [ ] Intercept DNS queries
- [ ] Block requests to specified domains
- [ ] Import hosts file for ad blocking

### 3.2 Lockdown Mode
Block all traffic except explicitly allowed apps.

**Tasks:**
- [ ] Add global lockdown toggle
- [ ] When enabled, only apps with explicit "allow" rule can connect
- [ ] Quick toggle widget

### 3.3 Roaming Rules
- [ ] Detect roaming state
- [ ] Apply roaming-specific rules
- [ ] Option to block all traffic when roaming

---

## Phase 4: Notifications & Alerts 🔔

### 4.1 Access Notifications
Notify when an app accesses the internet.

**Tasks:**
- [ ] Option to notify on first connection per app
- [ ] Option to notify on every connection
- [ ] Quick block/allow from notification

### 4.2 New App Detection
- [x] Register BroadcastReceiver for PACKAGE_ADDED - `PackageChangeReceiver.kt`
- [x] Notify when new app installed
- [x] Apply default rule (allow/block) from settings
- [x] Prompt to configure rules via notification tap

### 4.3 Network Speed in Status Bar
- [ ] Calculate current upload/download speed
- [ ] Show in persistent notification

---

## Phase 5: Settings & Configuration ⚙️

### 5.1 Settings Screen
Currently placeholder. Implement actual settings.

**Tasks:**
- [ ] Auto-start on boot
- [x] Theme selection (light/dark/system)
- [x] Default rule for new apps (allow/block)
- [x] Show/hide system apps by default
- [ ] Log retention period
- [ ] Export/Import rules
- [ ] Clear all rules

### 5.2 Backup & Restore
- [ ] Export rules to JSON
- [ ] Import rules from JSON
- [ ] Export traffic logs (CSV/JSON)

### 5.3 Auto-Start on Boot
- [ ] Register BOOT_COMPLETED receiver
- [ ] Start VPN service on boot (if enabled)

---

## Phase 6: iOS Support 🍎

Currently iOS has stub implementations.

### 6.1 Network Extension
- [ ] Implement NEPacketTunnelProvider
- [ ] Request VPN permission
- [ ] Similar sinkhole approach as Android

### 6.2 iOS-Specific UI
- [ ] Adapt UI for iOS conventions
- [ ] Handle iOS permissions flow

---

## Phase 7: Polish & Optimization ✨

### 7.1 Performance
- [ ] Profile VPN packet processing
- [ ] Optimize database queries
- [ ] Lazy loading for large app lists

### 7.2 UI/UX
- [ ] App icons in list (load async)
- [ ] Animations for state changes
- [ ] Empty states with illustrations
- [ ] Onboarding flow

### 7.3 Accessibility
- [ ] Content descriptions
- [ ] Screen reader support
- [ ] High contrast support

---

## Technical Debt

### High Priority
- [ ] Remove debug logs before release (see VpnService lines 77-78, 122-125)
- [ ] Handle VPN service resource leaks ("A resource failed to call close")
- [ ] Wrap debug logs in `BuildConfig.DEBUG` checks

### Medium Priority
- [ ] Add ProGuard/R8 rules
- [ ] Crashlytics/Analytics integration
- [x] Packet processing loop now used for traffic logging
- [ ] Use `ByteBuffer.allocateDirect()` for packet buffer for better performance

### Code Quality
- [ ] Extract `BlockedPackagesProvider` interface from RulesRepository for VPN service
- [ ] Add error reporting channel from VpnService to UI
- [ ] Add rules validation on DataStore load (handle corruption)
- [ ] Consider work profile / multi-user device support

---

## Features NOT Planned for MVP

These features are complex or not needed for initial release:

- SOCKS5 proxy support
- Port forwarding
- PCAP export
- Hosts file ad blocking (consider later)
- Tethering support
- Multiple VPN profiles

---

## Priority Order

1. **Phase 0** - Critical bug fixes (MUST do before release)
2. **Phase 5.1** - Settings screen (user configuration)
3. **Phase 2.1** - Real traffic logging
4. **Phase 1.2** - WiFi vs Mobile rules
5. **Phase 4.2** - New app detection
6. **Phase 1.4** - Uninstalled app cleanup
7. **Phase 3.2** - Lockdown mode
8. **Phase 5.3** - Auto-start on boot
9. Remaining phases as needed

---

## Edge Cases & Known Limitations

### Handled
- App updates: Package name stays the same, rules still apply
- Disabled apps: Filtered out of app list
- Apps without internet permission: Shown but marked (no effect if blocked)

### Not Yet Handled
- **Work profiles**: Apps in work profile have different UIDs - may need separate handling
- **Multi-user devices**: Different users have separate app instances
- **Instant apps**: May have different package behaviors
- **Split APKs**: Verify package name detection works correctly
- **VPN permission revocation**: No notification if user revokes while running
- **Battery optimization**: System may kill service - need to handle graceful restart

### By Design
- **VPN restart on rule change**: ~100-300ms interruption is unavoidable with builder-level blocking
- **No domain blocking**: Would require full packet processing (complex, Play Store prohibited)
- **Single VPN profile**: Android only allows one VPN at a time

---

## Architecture Decision Records

### ADR-001: Sinkhole Blocking Approach
**Decision:** Use `addAllowedApplication()` to route blocked apps through VPN tunnel and drop packets.

**Rationale:**
- Simple implementation (~300 LOC vs ~5000 LOC for packet filtering)
- No need for userspace TCP/UDP stack
- Google Play compliant (no domain/ad blocking)
- Reliable - blocking happens at OS level

**Trade-offs:**
- Cannot do per-domain blocking
- VPN restart required for rule changes

### ADR-002: DataStore for Rules Storage
**Decision:** Use DataStore with JSON serialization for rules persistence.

**Rationale:**
- Simple and sufficient for app-level rules
- Supports Flow observation for reactive updates
- No database setup required

**Trade-offs:**
- Entire rules list loaded/saved as single unit
- May need migration to SQLDelight if rules grow complex

---

## References

- [Android VpnService](https://developer.android.com/reference/android/net/VpnService)
- [iOS Network Extension](https://developer.apple.com/documentation/networkextension)
- [NetGuard Source](https://github.com/M66B/NetGuard) - Reference implementation
