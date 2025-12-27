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

## 5. Edge Cases

### 5.1 Rapid Toggle

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap Start/Stop rapidly 5 times | App should not crash |
| 2 | Wait for state to settle | Shows correct final state |

### 5.2 App Kill During VPN

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN protection | VPN active |
| 2 | Force stop BearGuard from settings | VPN stops |
| 3 | Check device connectivity | Internet works (no firewall) |

### 5.3 Device Reboot

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Start VPN, set some rules | VPN active, rules saved |
| 2 | Reboot device | Device restarts |
| 3 | Open BearGuard | Rules persist, VPN is stopped |

---

## 6. Navigation

### 6.1 Bottom Navigation

| Step | Action | Expected Result |
|------|--------|-----------------|
| 1 | Tap each nav item | Correct screen loads |
| 2 | Verify highlight | Active tab is highlighted |
| 3 | Tap same tab again | No crash, stays on screen |

### 6.2 Back Button

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
| 3.1 Empty Traffic | ☐ | ☐ | |
| 3.2 View Traffic | ☐ | ☐ | |
| 3.3 Clear Traffic | ☐ | ☐ | |
| 4.1 Blocking Works | ☐ | ☐ | |
| 4.2 Allowed Works | ☐ | ☐ | |
| 5.1 Rapid Toggle | ☐ | ☐ | |
| 5.2 App Kill | ☐ | ☐ | |
| 5.3 Reboot | ☐ | ☐ | |
| 6.1 Bottom Nav | ☐ | ☐ | |
| 6.2 Back Button | ☐ | ☐ | |

**Tested by:** _________________ **Date:** _________________

**Device:** _________________ **Android Version:** _________________
