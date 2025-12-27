# BearGuard Implementation Plan

A Kotlin Multiplatform firewall app for Android and iOS.

## Current Status

### Completed Features ✅

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

---

## Phase 1: Core Firewall Functionality 🔧

### 1.1 Actual Packet Blocking (HIGH PRIORITY)
Currently the VPN reads packets but doesn't actually block them based on rules.

**Approach:**
- Use `Builder.addDisallowedApplication(packageName)` to exclude blocked apps from VPN
- Apps excluded from VPN = no internet (sinkhole)
- Alternative: Parse packets, identify app by UID, drop packets for blocked apps

**Tasks:**
- [ ] Inject RulesRepository into VpnService
- [ ] On VPN start, get list of blocked packages
- [ ] Use `addDisallowedApplication()` for each allowed app (whitelist mode)
- [ ] Or use `addAllowedApplication()` for allowed apps only
- [ ] Listen for rule changes and restart VPN to apply

### 1.2 WiFi vs Mobile Data Rules
Allow different rules per network type.

**Tasks:**
- [ ] Extend Rule model: `isAllowedWifi`, `isAllowedMobile`
- [ ] Detect current network type (ConnectivityManager)
- [ ] Apply appropriate rules based on network
- [ ] Update UI to show WiFi/Mobile toggles per app

### 1.3 Screen On/Off Rules
Block when screen is off for battery savings.

**Tasks:**
- [ ] Add `allowWhenScreenOn` setting per app
- [ ] Register BroadcastReceiver for ACTION_SCREEN_ON/OFF
- [ ] Update VPN rules when screen state changes

---

## Phase 2: Traffic Logging & Analysis 📊

### 2.1 Real Traffic Logging
Parse actual packets and log connections.

**Approach:**
- Parse IP headers to extract source/dest IP and port
- Use `ConnectivityManager.getConnectionOwnerUid()` to map to app
- Store in database

**Tasks:**
- [ ] Implement IP header parsing (IPv4/IPv6)
- [ ] Extract protocol (TCP/UDP), ports, addresses
- [ ] Map connections to apps via UID
- [ ] Store connections in database (SQLDelight)
- [ ] Show real-time traffic in Traffic screen

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
- [ ] Register BroadcastReceiver for PACKAGE_ADDED
- [ ] Notify when new app installed
- [ ] Prompt to configure rules

### 4.3 Network Speed in Status Bar
- [ ] Calculate current upload/download speed
- [ ] Show in persistent notification

---

## Phase 5: Settings & Configuration ⚙️

### 5.1 Settings Screen
Currently placeholder. Implement actual settings.

**Tasks:**
- [ ] Auto-start on boot
- [ ] Theme selection (light/dark/system)
- [ ] Default rule for new apps (allow/block)
- [ ] Show/hide system apps by default
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

- [ ] Remove debug logs before release
- [ ] Handle VPN service resource leaks ("A resource failed to call close")
- [ ] Add ProGuard/R8 rules
- [ ] Crashlytics/Analytics integration

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

1. **Phase 1.1** - Actual packet blocking (core functionality)
2. **Phase 5.1** - Settings screen (user configuration)
3. **Phase 2.1** - Real traffic logging
4. **Phase 1.2** - WiFi vs Mobile rules
5. **Phase 4.2** - New app detection
6. **Phase 3.2** - Lockdown mode
7. **Phase 5.3** - Auto-start on boot
8. Remaining phases as needed

---

## References

- [Android VpnService](https://developer.android.com/reference/android/net/VpnService)
- [iOS Network Extension](https://developer.apple.com/documentation/networkextension)
