# SafeHaven Enhanced Emergency Alert System (SOS)

**Version**: 2.0
**Last Updated**: November 17, 2025
**Feature Status**: Enhanced Specification - Ready for Implementation

---

## Table of Contents

1. [System Overview](#system-overview)
2. [SOS Panic Button](#sos-panic-button)
3. [Emergency Alert Features](#emergency-alert-features)
4. [Shake-to-Delete Enhancement](#shake-to-delete-enhancement)
5. [Emergency Contacts Management](#emergency-contacts-management)
6. [Silent Evidence Capture During Emergency](#silent-evidence-capture-during-emergency)
7. [Offline Capabilities](#offline-capabilities)
8. [Technical Implementation](#technical-implementation)
9. [Safety Considerations](#safety-considerations)

---

## System Overview

SafeHaven's Emergency Alert System provides **multiple panic response options** designed to work in various danger scenarios. The system is built with survivor safety as the top priority, offering:

- **SOS Panic Button**: Single-touch emergency alert
- **Shake-to-Delete**: 3 rapid shakes to wipe evidence
- **Silent Mode**: All alerts work without sound, vibration, or screen changes
- **Offline Functionality**: Works without internet connection
- **Customizable Responses**: Configure what happens when each panic mode is activated

**Critical Design Principle**: The system must be **discoverable** (survivors can find and use it easily) but **discreet** (abusers won't notice it's happening).

---

## SOS Panic Button

### What is the SOS Panic Button?

**SOS (Save Our Souls)** is a dedicated emergency alert feature that sends immediate help signals to trusted contacts when activated. Unlike shake-to-delete (which destroys evidence), SOS **preserves evidence while summoning help**.

### How to Activate SOS

**Three activation methods** (user can choose which to enable):

#### Method 1: Long-Press Panic Button (DEFAULT)
- **Location**: Main screen, disguised as innocuous icon (e.g., "Wellness Check" button)
- **Activation**: Press and hold for 3 seconds
- **Visual Feedback**: Subtle progress ring (only visible to user)
- **Confirmation**: Silent vibration pattern (if enabled)

#### Method 2: Volume Button Sequence
- **Activation**: Press Volume Down 5 times rapidly
- **Advantage**: Works even when phone is locked or app is closed
- **Implementation**: Background service monitors volume button events

#### Method 3: Widget Tap
- **Location**: Home screen widget (looks like weather or calendar app)
- **Activation**: Single tap on widget
- **Advantage**: Fastest access, doesn't require opening app

### SOS Actions (Configurable)

When SOS is activated, the following actions occur **simultaneously**:

#### 1. Emergency Alerts Sent

**To Trusted Contacts** (up to 5 people):
- **SMS Message**: "EMERGENCY - [Your Name] needs immediate help. Location: [GPS coordinates + address]. Time: [timestamp]. This is an automated alert from SafeHaven."
- **Alternative Custom Message**: User can pre-write message (e.g., "I'm in danger, call police")
- **Follow-up messages**: Location updates every 5 minutes until deactivated

**To Emergency Services** (OPTIONAL, user must enable):
- **911 SMS** (in areas that support text-to-911)
- **Local Police Non-Emergency** (if user provides number)

#### 2. Location Sharing Activated

Even if GPS is normally disabled (for safety), SOS mode can:
- **Override GPS setting**: Temporarily turn on GPS (if user pre-authorized)
- **Share location**: Send coordinates + Google Maps link to emergency contacts
- **Continuous tracking**: Update location every 5 minutes
- **Location history**: Log all locations during SOS mode (encrypted, stored locally)

**Privacy Protection**:
- User decides in advance if GPS can be activated during SOS
- Location sharing automatically stops after 2 hours (or manual deactivation)
- Location data encrypted and never sent to SafeHaven servers

#### 3. Silent Audio Recording (OPTIONAL)

If pre-authorized by user:
- **Start recording**: Immediately begin silent audio recording
- **Duration**: Continue until stopped or 3-hour limit
- **Storage**: Encrypted and stored locally
- **Purpose**: Capture threats, admissions, or context for legal evidence
- **No indicators**: No on-screen display, no recording icon, no sound

**Legal Note**: Recording laws vary by state. App will warn user of one-party vs. two-party consent states. User accepts legal responsibility.

#### 4. Screen Disguise (OPTIONAL)

To hide that SOS is active:
- **Fake screen**: Display innocuous screen (e.g., "Weather App", "Recipe")
- **Normal appearance**: Phone looks like it's showing something innocent
- **Background operation**: All SOS functions run in background
- **Quick exit**: Swipe pattern returns to real screen

#### 5. Photo Capture (OPTIONAL)

If camera is accessible and user pre-authorized:
- **Front camera snapshot**: Silently capture photo of person in front of phone (potential abuser)
- **Rear camera snapshot**: Capture surroundings
- **No flash, no sound**: Completely silent
- **Automatic**: Happens every 30 seconds during SOS mode
- **Encrypted storage**: All photos immediately encrypted

#### 6. Screen Lock Override (OPTIONAL)

If user is forced to unlock phone:
- **Duress password trigger**: Entering duress password while SOS is active shows fake/empty data
- **Real password**: Shows actual data but keeps SOS running in background

---

### SOS Deactivation

**How to turn off SOS mode**:
1. **Safe Code**: Enter pre-set "safe code" (e.g., "1234#")
2. **Long-press again**: Press and hold panic button for 5 seconds
3. **Widget**: Tap widget again

**Sends "All Clear" message**: "All clear - I am safe now. Alert canceled."

**If not deactivated**: After 2 hours, automatic "Are you okay?" message sent to emergency contacts

---

## Emergency Alert Features

### Emergency Contact Management

**Location**: Settings → Emergency Contacts

**Fields per contact**:
- **Name**
- **Phone number**
- **Relationship** (friend, family, advocate, attorney)
- **Alert priority**: Primary (always alerted) or Secondary (alerted if no response from primary)
- **Custom message**: Optional personalized message for this contact

**Verification**:
- **Test alert**: Send test message to confirm contact receives alerts
- **Last tested date**: Track when contact was last verified

**Maximum contacts**: 5 primary + 5 secondary (total 10)

---

### Alert Escalation

If no response from emergency contacts within 15 minutes:
1. **Escalate to secondary contacts**: Alerts sent to secondary list
2. **Repeat alerts**: Primary contacts receive second alert
3. **Optional 911 call**: If user pre-authorized, automatic 911 call placed

**User control**: User can disable escalation if concerned about over-alerting

---

### False Alarm Protection

**To prevent accidental activation**:
1. **Confirmation required**: After long-press, user must confirm (unless skipped in settings)
2. **Cancel window**: 5-second countdown before alerts sent
3. **Easy deactivation**: Simple cancellation process

**If accidentally activated**:
- Cancel within 5 seconds: No alerts sent
- Cancel after 5 seconds: "False alarm, I'm safe" message sent to contacts

---

## Shake-to-Delete Enhancement

**Existing feature** (from original spec) with enhancements:

### Current Functionality
- **3 rapid shakes**: Detected by accelerometer
- **Immediate deletion**: All evidence wiped in <2 seconds
- **Secure erasure**: Files overwritten with random data

### NEW ENHANCEMENTS

#### 1. Pre-Delete Alert (OPTIONAL)
Before wiping evidence, send emergency alert:
- **1-second alert**: "Emergency - evidence being deleted" sent to emergency contacts
- **Rationale**: If survivor is forced to delete evidence, contacts are notified
- **Toggle**: User can disable if they prefer instant deletion with no trace

#### 2. Cloud Backup Preservation (OPTIONAL)
If user has cloud backup enabled:
- **Evidence preserved in cloud**: Shake-to-delete only wipes local device
- **Cloud backup protected**: Requires separate password to access
- **Use case**: Survivor can restore evidence later from safe location

#### 3. Confirmation Option (OPTIONAL)
- **Confirmation screen**: "Delete all evidence? Shake again to confirm."
- **Prevents accidents**: Reduces risk of accidental deletion
- **Toggle**: User can disable for instant wipe (faster but more risky)

#### 4. Partial Wipe Options
Instead of deleting everything, user can configure what gets deleted:
- **Evidence only**: Photos, videos, audio (keeps incident reports)
- **Recent evidence only**: Last 7 days (preserves older documentation)
- **Everything**: Full wipe (current default)

**Configuration**: Settings → Panic Delete → What to Delete

---

## Emergency Contacts Management

### Contact Groups

Organize contacts by purpose:
- **Immediate Help**: Friends/family who can physically help
- **Legal/Advocacy**: Attorneys, DV advocates
- **Emotional Support**: Therapists, hotline numbers
- **Professional**: Employers (if workplace violence is a concern)

### Smart Contact Selection

**Scenario-based alerts**:
- **Home emergency**: Alert neighbors + police
- **Work emergency**: Alert HR + security
- **Stalking**: Alert all contacts + police

User can create different "alert profiles" for different situations.

---

## Silent Evidence Capture During Emergency

### Real-Time Documentation

While SOS is active, survivor can document:
- **Silent photos**: Using volume button or widget
- **Silent video**: Background recording
- **Audio recording**: Capture threats, confessions
- **GPS tracking**: Movement logged every 30 seconds
- **Timestamped log**: Everything marked with precise time

### Auto-Evidence Mode

When SOS is activated:
- **Automatic photo capture**: Every 30 seconds (if safe)
- **Audio recording**: Continuous
- **All evidence encrypted**: Immediate encryption
- **Cloud sync** (if enabled): Evidence sent to cloud in real-time

**Purpose**: If phone is taken or destroyed, evidence is preserved.

---

## Offline Capabilities

### Offline SMS Alerts

**How it works**:
- SOS alerts use **SMS (text messages)**, not internet
- Works even without Wi-Fi or mobile data
- Requires cellular signal only

**Fallback if no signal**:
- Alerts queued and sent when signal restored
- Local evidence still captured and encrypted

### Offline Evidence Capture

All emergency features work offline:
- **Photos/video**: Stored locally, encrypted
- **Audio recording**: Saved to device storage
- **GPS**: May use last known location if no signal
- **Incident log**: Created locally

**Sync when online**: All evidence synced to cloud when connection restored

---

## Technical Implementation

### Architecture

**Components**:

1. **EmergencyAlertManager.kt**
   - Coordinates all SOS functions
   - Manages activation methods
   - Handles alert sending
   - Controls recording and photo capture

2. **EmergencyContact.kt** (Entity)
   ```kotlin
   @Entity(tableName = "emergency_contacts")
   data class EmergencyContact(
       @PrimaryKey val id: String = UUID.randomUUID().toString(),
       val userId: String,
       val name: String,
       val phoneNumber: String,
       val relationship: String,
       val isPrimary: Boolean = true,
       val customMessage: String? = null,
       val lastTested: Long? = null,
       val isActive: Boolean = true
   )
   ```

3. **SOSSession.kt** (Entity)
   ```kotlin
   @Entity(tableName = "sos_sessions")
   data class SOSSession(
       @PrimaryKey val id: String = UUID.randomUUID().toString(),
       val userId: String,
       val activatedAt: Long,
       val deactivatedAt: Long? = null,
       val alertsSent: Int,
       val evidenceCaptured: String, // JSON array of evidence IDs
       val locationUpdates: String, // JSON array of {lat, lon, timestamp}
       val wasCanceled: Boolean = false,
       val cancelReason: String? = null
   )
   ```

4. **PanicButtonWidget** (Android Widget)
   - Home screen widget for quick access
   - Configurable appearance (disguised as other app)
   - Single-tap activation

5. **VolumeButtonListener.kt** (Background Service)
   - Monitors volume button presses
   - Detects 5-press sequence
   - Works even when phone is locked

6. **ShakeDetector.kt** (Enhanced)
   - Original shake-to-delete functionality
   - New: Optional pre-delete alert
   - New: Partial wipe options

---

### User Flow Diagrams

#### SOS Activation Flow
```
User in danger
    ↓
[Long-press panic button / Volume buttons 5x / Tap widget]
    ↓
[5-second countdown with cancel option] (if confirmation enabled)
    ↓
[Simultaneous actions]:
    • Send SMS to emergency contacts
    • Activate GPS (if authorized)
    • Start audio recording (if authorized)
    • Begin photo capture (if authorized)
    • Display disguise screen (if enabled)
    ↓
[Continuous monitoring]:
    • Location updates every 5 minutes
    • Photos every 30 seconds
    • Audio recording until stopped
    ↓
[Deactivation]:
    • User enters safe code
    • OR long-press panic button again
    ↓
"All clear" message sent to contacts
SOS session logged (encrypted)
```

#### Shake-to-Delete Flow
```
User in immediate danger (phone about to be taken)
    ↓
[Shake phone 3 times rapidly]
    ↓
[Optional: Send alert to emergency contacts] (if enabled)
    ↓
[Optional: Confirmation shake] (if enabled)
    ↓
[Delete]:
    • Evidence only (photos/videos/audio)
    • OR Recent evidence only (last 7 days)
    • OR Everything (full wipe)
    ↓
[Secure erasure]:
    • Overwrite with random data
    • Delete database records
    • Clear file system cache
    ↓
[Cloud backup preserved] (if enabled)
    ↓
App shows empty state (if duress password entered) or locks
```

---

### Settings Configuration

**Location**: Settings → Emergency Alerts

**Options**:

```
[Emergency Alerts]

▸ Emergency Contacts (5 configured)
  - Add Contact
  - Test All Contacts

▸ SOS Panic Button
  ☑ Enable SOS Panic Button
  ☑ Long-press activation (3 seconds)
  ☑ Volume button activation (5 presses)
  ☑ Widget activation
  ☑ Require confirmation (5-second countdown)
  ☐ Escalate to secondary contacts after 15 min
  ☐ Automatic 911 call if no response (30 min)

▸ SOS Actions
  ☑ Send SMS to emergency contacts
  ☑ Temporarily enable GPS for location sharing
  ☑ Send location updates every 5 minutes
  ☐ Start silent audio recording
  ☐ Capture photos every 30 seconds
  ☐ Display disguise screen

  Disguise screen:  [Weather ▼]
  Auto-deactivate after:  [2 hours ▼]

▸ Panic Delete (Shake-to-Delete)
  ☑ Enable shake-to-delete
  Shake sensitivity:  [Medium ▼]
  ☐ Send alert before deleting
  ☐ Require confirmation shake
  ☐ Preserve cloud backup

  What to delete:
    ○ Evidence only (photos/videos/audio)
    ○ Recent evidence (last 7 days)
    ● Everything (full wipe)

▸ Safe Code
  Current code:  [****]
  [Change Safe Code]

▸ Test Emergency System
  [Send Test Alert]
  [Test Panic Delete] (uses dummy data)
```

---

### Permissions Required

**Android Permissions**:
- `SEND_SMS` - Send emergency text messages
- `ACCESS_FINE_LOCATION` - GPS for location sharing (if user enables)
- `RECORD_AUDIO` - Silent audio recording (if user enables)
- `CAMERA` - Silent photo capture (if user enables)
- `CALL_PHONE` - Automatic 911 call (if user enables)
- `FOREGROUND_SERVICE` - Keep SOS running when app is backgrounded
- `RECEIVE_BOOT_COMPLETED` - Restart emergency services after phone reboot

All permissions are **optional** except `SEND_SMS` (core SOS functionality). App explains why each permission is needed and allows granular control.

---

## Safety Considerations

### Abuser Discovery Risk

**Risks**:
- Abuser finds emergency contacts list
- Abuser discovers SOS button
- Evidence of emergency alerts found

**Mitigations**:

1. **Password-protected settings**: Emergency settings require password to access
2. **Disguised button**: SOS button looks like normal app feature ("Wellness Check", "Daily Quote")
3. **Encrypted contact list**: Emergency contacts stored encrypted
4. **Hide from recent apps**: App can be configured not to appear in recent apps list
5. **Alert deletion**: SMS alerts can be auto-deleted from sender's phone (if supported)

### False Positives

**Risks**:
- Accidental activation
- Shake-to-delete triggered unintentionally
- Contacts alarmed unnecessarily

**Mitigations**:
1. **Confirmation required**: 5-second countdown with cancel option
2. **Shake sensitivity**: Adjustable threshold
3. **Test mode**: Practice without sending real alerts
4. **False alarm message**: Easy to send "I'm safe, false alarm" message

### Legal Considerations

**Recording consent**:
- App must comply with state recording laws
- User warned of one-party vs. two-party consent requirements
- User accepts legal responsibility for recordings

**911 calls**:
- Automatic 911 calls must be opt-in (can't be default)
- User warned that false 911 calls are illegal
- Confirmation required before enabling

**Liability**:
- App disclaimers: SafeHaven is a tool, not a substitute for professional help
- No guarantee of contact response or help arrival
- User responsible for vetting emergency contacts

---

## User Education

### Onboarding Flow

When user first sets up SOS:
1. **Explain feature**: "Set up emergency alerts to protect yourself"
2. **Add contacts**: "Choose up to 5 people who can help in an emergency"
3. **Test system**: "Send a test alert to make sure it works"
4. **Configure actions**: "Choose what happens when you activate SOS"
5. **Practice**: "Try activating (test mode) so you're ready"

### In-App Tutorials

**Video/Illustrated guides**:
- How to activate SOS
- How to add emergency contacts
- How shake-to-delete works
- What to do after sending an alert

### Emergency Contact Instructions

When someone is added as an emergency contact, SafeHaven sends them:
- **Introduction SMS**: "You've been added as an emergency contact for [Name] in the SafeHaven app."
- **Instructions**: "If you receive an alert, this person may be in danger. Consider calling police or checking on them immediately."
- **Test alert**: So they know what real alerts look like

---

## Future Enhancements

### Potential Features (Future Versions)

1. **Live Location Streaming**: Real-time location visible to emergency contacts via web link
2. **Video Recording**: Silent video capture during SOS
3. **Wearable Integration**: Activate SOS from smartwatch
4. **Automated 911**: Voice call to 911 with pre-recorded message
5. **Safe Word**: Specific word spoken triggers SOS (voice recognition)
6. **Geofencing**: Auto-activate SOS if entering dangerous location
7. **Beacon Mode**: Phone emits loud alarm and flashing lights (opposite of silent mode)
8. **Community Network**: Alert nearby SafeHaven users in community watch network

---

## Integration with Existing Features

### Relationship to Current Features

| Existing Feature | SOS Integration |
|------------------|-----------------|
| **Silent Camera** | Used for automatic photo capture during SOS |
| **Incident Reports** | SOS session automatically creates incident report |
| **Evidence Vault** | All SOS-captured evidence stored in vault |
| **Dual Password** | Duress password hides SOS session history |
| **Panic Delete** | Alternative to SOS (wipe vs. alert) |
| **GPS Settings** | SOS can override GPS setting temporarily |
| **Cloud Backup** | SOS evidence auto-backed up if enabled |

---

## Implementation Checklist

**For 24-Hour Sprint**:

### Phase 1: Core SOS (Priority 1)
- [ ] Create EmergencyContact entity and DAO
- [ ] Create SOSSession entity and DAO
- [ ] Build EmergencyAlertManager
- [ ] Implement long-press panic button activation
- [ ] SMS sending to emergency contacts
- [ ] Location sharing (if GPS enabled)
- [ ] Safe code deactivation
- [ ] Settings UI for emergency contacts

### Phase 2: Enhanced Activation (Priority 2)
- [ ] Volume button listener service
- [ ] Home screen widget
- [ ] Confirmation countdown UI
- [ ] Alert escalation logic

### Phase 3: Advanced Features (Priority 3)
- [ ] Silent audio recording during SOS
- [ ] Automatic photo capture
- [ ] Disguise screen
- [ ] Partial wipe options for shake-to-delete
- [ ] Pre-delete alert for shake-to-delete

### Phase 4: Polish (Priority 4)
- [ ] Onboarding tutorial
- [ ] Test mode
- [ ] Emergency contact introduction SMS
- [ ] False alarm messaging

---

## Conclusion

The **SafeHaven Enhanced Emergency Alert System** provides survivors with powerful, discreet tools to summon help and document danger in real-time. By combining the **SOS Panic Button** (alert help) with the existing **Shake-to-Delete** feature (destroy evidence), survivors can respond appropriately to any threat scenario.

**Key Principles**:
- **User control**: Survivors decide what features to enable
- **Offline-first**: Works without internet
- **Silent operation**: No visible/audible indicators
- **Evidence preservation**: Real-time documentation and cloud backup
- **Fast activation**: Multiple methods for quick access

This system can save lives.

---

**For immediate danger, always call 911.**
**National Domestic Violence Hotline: 1-800-799-7233**
