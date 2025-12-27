# BearGuard Manual Test Plan

## Prerequisites

- Android device or emulator (API 24+)
- App installed and permissions granted

---

## 1. VPN Control

### 1.1 Start VPN Protection

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Open app | Home screen shows "Protection Inactive" with gray shield |
| 2 | Tap "Start Protection" | VPN permission dialog appears (first time only) |
| 3 | Grant VPN permission | Button shows loading spinner |
| 4 | Wait for VPN to start | Shield turns green, text shows "Protection Active" |
| 5 | Check notification bar | BearGuard notification appears: "Firewall is protecting your device" |

### 1.2 Stop VPN Protection

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | With VPN active, tap "Stop Protection" | Button shows loading spinner |
| 2 | Wait for VPN to stop | Shield turns gray, text shows "Protection Inactive" |
| 3 | Check notification bar | BearGuard notification disappears |

### 1.3 VPN Persistence

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN protection | VPN active |
| 2 | Close app (swipe away) | VPN remains active (check notification) |
| 3 | Reopen app | Home screen shows "Protection Active" |

---

## 2. App Rules

### 2.1 View App List

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap "Apps" in bottom nav | App list screen loads |
| 2 | Wait for apps to load | List shows installed apps with toggle switches |
| 3 | Verify default state | All apps show "Internet access allowed" (toggles ON) |

### 2.2 Block an App

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Find an app (e.g., Chrome) | App shows with toggle ON |
| 2 | Toggle switch OFF | Card background turns red-tinted |
| 3 | Verify text | Shows "Blocked" under app name |
| 4 | Close and reopen app | Rule persists - app still blocked |

### 2.3 Unblock an App

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Find a blocked app | Card shows red-tinted, "Blocked" |
| 2 | Toggle switch ON | Card returns to normal color |
| 3 | Verify text | Shows "Internet access allowed" |

### 2.4 Search Apps

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap search field | Keyboard appears |
| 2 | Type "chrome" | List filters to matching apps |
| 3 | Clear search | Full list returns |

### 2.5 Show System Apps

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Note app count | e.g., "25 apps" |
| 2 | Tap "Show system apps" chip | Chip becomes selected |
| 3 | Wait for reload | More apps appear in list |
| 4 | Tap chip again | Returns to user apps only |

---

## 3. Traffic Monitoring

### 3.1 View Traffic (Empty State)

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap "Traffic" in bottom nav | Traffic screen loads |
| 2 | With no VPN activity | Shows "No traffic logged yet" |

### 3.2 View Traffic (With Activity)

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN protection | VPN active |
| 2 | Open browser, load a page | Generate network traffic |
| 3 | Return to BearGuard Traffic tab | Connections appear in list |
| 4 | Verify connection info | Shows app name, destination IP:port, protocol |

### 3.3 Clear Traffic Log

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | With traffic logged | Connections visible |
| 2 | Tap trash icon in top bar | List clears |
| 3 | Verify | Shows "No traffic logged yet" |

### 2.6 WiFi vs Mobile Data Rules

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Find any app in the list | App shows with main toggle |
| 2 | Toggle main access OFF | WiFi/Mobile chips disappear |
| 3 | Toggle main access ON | WiFi/Mobile chips appear |
| 4 | Toggle WiFi OFF (keep Mobile ON) | WiFi chip shows "blocked" style |
| 5 | Connect to WiFi, start VPN | App blocked on WiFi |
| 6 | Connect to Mobile data | App works on Mobile |

---

## 4. Firewall Functionality

### 4.1 Verify Blocking Works

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Go to Apps tab | App list visible |
| 2 | Block Chrome (or test browser) | Toggle OFF |
| 3 | Start VPN protection | VPN active |
| 4 | Open Chrome, try to load google.com | Page fails to load / timeout |
| 5 | Return to BearGuard, unblock Chrome | Toggle ON |
| 6 | Try loading page again | Page loads successfully |

### 4.2 Verify Allowed Apps Work

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Ensure app is allowed (toggle ON) | Default state |
| 2 | Start VPN protection | VPN active |
| 3 | Use the allowed app | Network works normally |

---

## 5. Settings

### 5.1 Theme Selection

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Open Settings tab | Settings screen loads |
| 2 | Select "Light" theme | App immediately changes to light mode |
| 3 | Select "Dark" theme | App immediately changes to dark mode |
| 4 | Select "System" theme | App follows system theme |

### 5.2 Default Rule for New Apps

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Open Settings | Default rule section visible |
| 2 | Select "Block" | Setting saved |
| 3 | Install a new app | New app should have blocked rule by default |
| 4 | Select "Allow" | Setting saved |
| 5 | Install another app | New app should be allowed by default |

### 5.3 Show System Apps Default

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Disable "Show system apps by default" | Toggle OFF |
| 2 | Go to Apps tab | Only user apps shown |
| 3 | Enable setting | Toggle ON |
| 4 | Go to Apps tab | System apps shown by default |

### 5.4 Lockdown Mode

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Go to Settings > Security | Lockdown mode toggle visible |
| 2 | Enable Lockdown Mode | Toggle ON, VPN restarts if active |
| 3 | Start VPN protection | VPN active |
| 4 | Open an app WITHOUT explicit allow rule | App cannot connect (blocked) |
| 5 | Go to Apps, toggle app ON | App gets explicit allow rule |
| 6 | Try app again | App can now connect |
| 7 | Disable Lockdown Mode | All apps work normally again |

### 5.5 Auto-Start on Boot

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Go to Settings > Startup | Auto-start toggle visible |
| 2 | Enable "Auto-start on boot" | Toggle ON |
| 3 | Reboot device | Device restarts |
| 4 | Check notification bar | BearGuard VPN starts automatically |
| 5 | Disable "Auto-start on boot" | Toggle OFF |
| 6 | Reboot device | VPN does not auto-start |

---

## 6. New App Detection

### 6.1 New App Installation Notification

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Install a new app from Play Store | Installation completes |
| 2 | Check notification bar | BearGuard notification shows "New app: [AppName]" |
| 3 | Check notification content | Shows "Internet access allowed" or "blocked" based on default rule |
| 4 | Tap notification | Opens BearGuard to Apps screen |

### 6.2 App Uninstall Cleanup

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Go to Apps, create a rule for an app | Block or allow the app |
| 2 | Uninstall that app | App removed from device |
| 3 | Reinstall the same app | App should get fresh default rule (old rule was cleaned up) |

---

## 7. Edge Cases

### 7.1 Rapid Toggle

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap Start/Stop rapidly 5 times | App should not crash |
| 2 | Wait for state to settle | Shows correct final state |

### 7.2 App Kill During VPN

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN protection | VPN active |
| 2 | Force stop BearGuard from settings | VPN stops |
| 3 | Check device connectivity | Internet works (no firewall) |

### 7.3 Device Reboot

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN, set some rules | VPN active, rules saved |
| 2 | Reboot device | Device restarts |
| 3 | Open BearGuard | Rules persist, VPN is stopped |

---

## 8. Navigation

### 8.1 Bottom Navigation

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap each nav item | Correct screen loads |
| 2 | Verify highlight | Active tab is highlighted |
| 3 | Tap same tab again | No crash, stays on screen |

### 8.2 Back Button

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Navigate to Apps tab | Apps screen visible |
| 2 | Press back button | App closes (single back stack) |

---

## Test Results

| Test | Pass | Fail | Notes |
|------|------|------|-------|
| 1.1 Start VPN | ☐ | ☐ | |
| 1.2 Stop VPN | ☐ | ☐ | |
| 1.3 VPN Persistence | ☐ | ☐ | |
| 2.1 View App List | ☐ | ☐ | |
| 2.2 Block App | ☐ | ☐ | |
| 2.3 Unblock App | ☐ | ☐ | |
| 2.4 Search Apps | ☐ | ☐ | |
| 2.5 System Apps | ☐ | ☐ | |
| 2.6 WiFi/Mobile Rules | ☐ | ☐ | |
| 3.1 Empty Traffic | ☐ | ☐ | |
| 3.2 View Traffic | ☐ | ☐ | |
| 3.3 Clear Traffic | ☐ | ☐ | |
| 4.1 Blocking Works | ☐ | ☐ | |
| 4.2 Allowed Works | ☐ | ☐ | |
| 5.1 Theme Selection | ☐ | ☐ | |
| 5.2 Default Rule | ☐ | ☐ | |
| 5.3 System Apps Default | ☐ | ☐ | |
| 5.4 Lockdown Mode | ☐ | ☐ | |
| 5.5 Auto-Start Boot | ☐ | ☐ | |
| 6.1 New App Notification | ☐ | ☐ | |
| 6.2 Uninstall Cleanup | ☐ | ☐ | |
| 7.1 Rapid Toggle | ☐ | ☐ | |
| 7.2 App Kill | ☐ | ☐ | |
| 7.3 Reboot | ☐ | ☐ | |
| 8.1 Bottom Nav | ☐ | ☐ | |
| 8.2 Back Button | ☐ | ☐ | |

**Tested by:** _________________ **Date:** _________________

**Device:** _________________ **Android Version:** _________________
