# Project Renaming Plan - NeuroThrive → DivergentThrive

**Date**: November 18, 2025
**Reason**: "NeuroThrive" is a trademarked name
**New Name**: DivergentThrive (verified clear - no conflicts)
**SafeHaven Status**: Also trademarked, replacement name TBD

---

## 📋 Renaming Scope

### What Needs to Change Now:
- ✅ **NeuroThrive** → **DivergentThrive** (confirmed)

### What to Change Later (After SafeHaven Replacement Chosen):
- ⏳ **SafeHaven** → **[TBD]** (user hasn't chosen yet)
- ⏳ **NeuroThrive Plus** → **DivergentThrive Plus** (depends on integration strategy)

---

## 🔍 Complete File Inventory

### Files Containing "NeuroThrive" or "neurothrive"

#### NeuroThrive PWA Repository (16 files)

**Critical Files (Code/Config)**:
1. [manifest.json](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\manifest.json)
   - App name
   - Short name
   - Description
   - Start URL

2. [index.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\index.html)
   - Page title
   - App name in UI
   - Meta tags
   - Branding text

3. [sw.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\sw.js)
   - Cache name
   - Service worker ID

4. [js/config.template.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\js\config.template.js)
   - OAuth redirect URLs
   - Config comments

5. [js/salesforce-api.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\js\salesforce-api.js)
   - API references
   - OAuth callbacks

6. [js/sync-manager.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\js\sync-manager.js)
   - Sync configuration
   - Comments

7. [oauth/callback](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\oauth\callback)
   - OAuth callback handler

**Documentation Files**:
8. [README.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\README.md)
9. [QUICK_DEPLOY.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\QUICK_DEPLOY.md)
10. [secret-unlock-template.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\secret-unlock-template.html)
11. [PHASE1_TESTING_GUIDE.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\PHASE1_TESTING_GUIDE.md)
12. [GITHUB_SESSION_SETUP_CHECKLIST.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\GITHUB_SESSION_SETUP_CHECKLIST.md)
13. [GITHUB_CLAUDE_TRANSFER_PACKAGE.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\GITHUB_CLAUDE_TRANSFER_PACKAGE.md)
14. [docs/PWA_SALESFORCE_INTEGRATION_ARCHITECTURE.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\docs\PWA_SALESFORCE_INTEGRATION_ARCHITECTURE.md)
15. [docs/PWA_SYNC_DEPLOYMENT_COMPLETE.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\docs\PWA_SYNC_DEPLOYMENT_COMPLETE.md)
16. [docs/OAUTH_CONNECTED_APP_SETUP.md](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\docs\OAUTH_CONNECTED_APP_SETUP.md)

---

#### SafeHaven-Build Repository (100+ files)

**Critical Code Files (Package Names)**:

**App Package Structure** - ALL need package rename from `app.neurothrive.safehaven` to `app.divergentthrive.safehaven` (or new SafeHaven name):

**safehaven-core Library** (29 files):
1-29. All Kotlin files in `safehaven-core/src/main/java/app/neurothrive/safehaven/`
   - Package declarations: `package app.neurothrive.safehaven.*`
   - Import statements: `import app.neurothrive.safehaven.*`

**Main App** (40+ files):
30-70+. All Kotlin files in `app/src/main/java/app/neurothrive/safehaven/`
   - Package declarations
   - Import statements
   - Theme references
   - ViewModel references

**Build Configuration**:
- [safehaven-core/build.gradle.kts](C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build\safehaven-core\build.gradle.kts)
  - Namespace: `namespace = "app.neurothrive.safehaven"`

**Documentation Files** (35 files):
- CORRECTED_SALESFORCE_STATUS.md
- MARKET_ANALYSIS_FEATURE_LIST.md
- COMPLETE_BUILD_SYNOPSIS.md
- OPTION1_IMPLEMENTATION_STATUS.md
- COMPLETE_INTEGRATION_STRATEGY.md
- NEUROTHRIVE_INTEGRATION_GUIDE.md
- BUILD_STATUS.md
- UPDATED_STEALTH_ASSESSMENT.md
- STEALTH_INTEGRATION_ACTION_PLAN.md
- PANIC_DELETE_IMPLEMENTATION_GUIDE.md
- And 25+ more documentation files

**README Files**:
- [README.md](C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build\README.md)
- [safehaven-core/README.md](C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build\safehaven-core\README.md)

---

## 🔧 Renaming Strategy

### Phase 1: DivergentThrive Renaming (NOW)

**Repository Renaming**:
1. Rename GitHub repository: `neurothrive-pwa` → `divergentthrive-pwa`
2. Update local directory name (optional)
3. Update git remote URL

**Code Changes (PWA)**:
1. Update [manifest.json](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\manifest.json):
   ```json
   {
     "name": "DivergentThrive",
     "short_name": "DivergentThrive",
     "description": "Your neurodivergent support companion"
   }
   ```

2. Update [index.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\index.html):
   - `<title>NeuroThrive</title>` → `<title>DivergentThrive</title>`
   - All visible "NeuroThrive" text in UI
   - Meta tags

3. Update [sw.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\sw.js):
   ```javascript
   const CACHE_NAME = 'divergentthrive-v1.2.3';
   ```

4. Update OAuth configuration files:
   - [js/config.template.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\js\config.template.js)
   - [js/salesforce-api.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\js\salesforce-api.js)
   - [oauth/callback](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\oauth\callback)

5. Update deep link scheme (CRITICAL):
   - Current: `neurothrive://safehaven/unlock`
   - New: `divergentthrive://safehaven/unlock`
   - Files affected:
     - [secret-unlock-template.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\secret-unlock-template.html) (line 174)

**Documentation Changes**:
- Find/replace "NeuroThrive" → "DivergentThrive" in all 8 documentation files
- Find/replace "neurothrive" → "divergentthrive" (lowercase) in code examples

**Salesforce Changes**:
- Connected App name
- OAuth callback URL: `https://abbyluggery.github.io/divergentthrive-pwa/oauth/callback`
- Custom metadata references

---

### Phase 2: SafeHaven Renaming (AFTER New Name Chosen)

**When User Chooses Replacement Name**, we'll need to update:

**Android Package Names** (100+ files):
- All Kotlin files: `package app.neurothrive.safehaven.*` → `package app.divergentthrive.[newname].*`
- All imports: `import app.neurothrive.safehaven.*` → `import app.divergentthrive.[newname].*`

**Build Configuration**:
- [app/build.gradle.kts](C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build\app\build.gradle.kts):
  ```kotlin
  applicationId = "app.divergentthrive.[newname]"
  namespace = "app.divergentthrive.[newname]"
  ```

- [safehaven-core/build.gradle.kts](C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build\safehaven-core\build.gradle.kts):
  ```kotlin
  namespace = "app.divergentthrive.[newname]"
  ```

**Deep Link Updates**:
- AndroidManifest.xml intent filter:
  ```xml
  <data
      android:scheme="divergentthrive"
      android:host="[newname]"
      android:pathPrefix="/unlock" />
  ```

**Branding**:
- App name in strings.xml
- Play Store listing
- Documentation references

---

## 📝 Detailed Replacement Checklist

### NeuroThrive PWA Files (Priority Order)

#### 1. manifest.json (CRITICAL - Affects Install)
**Location**: Root directory
**Changes**:
```diff
- "name": "NeuroThrive",
+ "name": "DivergentThrive",
- "short_name": "NeuroThrive",
+ "short_name": "DivergentThrive",
- "description": "NeuroThrive - Your neurodivergent support companion"
+ "description": "DivergentThrive - Your neurodivergent support companion"
```

#### 2. index.html (CRITICAL - User-Facing)
**Location**: Root directory
**Line Estimates**: ~1,731 lines, multiple occurrences
**Changes**:
- Line ~6: `<title>NeuroThrive</title>` → `<title>DivergentThrive</title>`
- Line ~800-1000: All UI text containing "NeuroThrive"
- Meta tags in `<head>`
- About section text
- Settings tab text (if integrated)

**Example Occurrences**:
```html
<!-- OLD -->
<h1>Welcome to NeuroThrive</h1>
<p>NeuroThrive helps you track your wellness...</p>

<!-- NEW -->
<h1>Welcome to DivergentThrive</h1>
<p>DivergentThrive helps you track your wellness...</p>
```

#### 3. sw.js (CRITICAL - Service Worker Cache)
**Location**: Root directory
**Changes**:
```diff
- const CACHE_NAME = 'neurothrive-v1.2.3';
+ const CACHE_NAME = 'divergentthrive-v1.2.3';
```

**NOTE**: Changing cache name will force cache refresh for all users.

#### 4. secret-unlock-template.html (CRITICAL - Deep Link)
**Location**: Root directory
**Changes**:
```diff
Line 174:
- const deepLinkUrl = 'neurothrive://safehaven/unlock';
+ const deepLinkUrl = 'divergentthrive://safehaven/unlock';

Line 181:
- const playStoreUrl = 'https://play.google.com/store/apps/details?id=app.neurothrive.safehaven.plus';
+ const playStoreUrl = 'https://play.google.com/store/apps/details?id=app.divergentthrive.safehaven.plus';

Line 186:
- 'NeuroThrive Plus provides enhanced privacy and data protection features.'
+ 'DivergentThrive Plus provides enhanced privacy and data protection features.'

Line 102:
- <h3>About NeuroThrive</h3>
+ <h3>About DivergentThrive</h3>
```

#### 5. js/config.template.js (Important - OAuth Config)
**Location**: js/
**Changes**: Comment references, configuration examples

#### 6. js/salesforce-api.js (Important - API Calls)
**Location**: js/
**Changes**: Comments, error messages, API references

#### 7. js/sync-manager.js (Low Priority - Comments)
**Location**: js/
**Changes**: Comment references only

#### 8. oauth/callback (Important - OAuth Redirect)
**Location**: oauth/
**Changes**: References in callback handler

---

### Documentation Files (Can Use Bulk Replace)

**All Documentation Files** - Find/Replace:
- "NeuroThrive" → "DivergentThrive" (case-sensitive)
- "neurothrive" → "divergentthrive" (lowercase)
- "neurothrive-pwa" → "divergentthrive-pwa" (repository name)

**Files to Update**:
1. README.md
2. QUICK_DEPLOY.md
3. PHASE1_TESTING_GUIDE.md
4. GITHUB_SESSION_SETUP_CHECKLIST.md
5. GITHUB_CLAUDE_TRANSFER_PACKAGE.md
6. docs/PWA_SALESFORCE_INTEGRATION_ARCHITECTURE.md
7. docs/PWA_SYNC_DEPLOYMENT_COMPLETE.md
8. docs/OAUTH_CONNECTED_APP_SETUP.md

---

### SafeHaven-Build Repository Files

**Documentation Only (For Now)**:
- Find/Replace "NeuroThrive" → "DivergentThrive" in all 35+ markdown files
- Leave code package names as `app.neurothrive.safehaven` until SafeHaven replacement name chosen
- Update references to PWA repository name

**Critical Documentation Files**:
1. CORRECTED_SALESFORCE_STATUS.md
2. MARKET_ANALYSIS_FEATURE_LIST.md
3. COMPLETE_BUILD_SYNOPSIS.md
4. OPTION1_IMPLEMENTATION_STATUS.md
5. COMPLETE_INTEGRATION_STRATEGY.md
6. NEUROTHRIVE_INTEGRATION_GUIDE.md
7. And 29+ more

---

## 🚨 Critical Changes Required Immediately

### 1. Deep Link Scheme Change
**Current**: `neurothrive://safehaven/unlock`
**New**: `divergentthrive://safehaven/unlock`

**Impact**: Breaks integration if not updated in BOTH PWA and Android

**Files to Update**:
- PWA: [secret-unlock-template.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\secret-unlock-template.html) (line 174)
- Android: AndroidManifest.xml (when deep linking implemented in Day 3)

### 2. Play Store Package ID
**Current**: `app.neurothrive.safehaven.plus`
**New**: `app.divergentthrive.safehaven.plus` OR `app.divergentthrive.[newname].plus`

**Decision Needed**:
- Option A: Change only "neurothrive" → "divergentthrive" now, keep "safehaven"
- Option B: Wait until SafeHaven replacement chosen, change both at once

**Recommendation**: Option A (change neurothrive now, safehaven later)

### 3. GitHub Repository URL
**Current**: https://github.com/abbyluggery/neurothrive-pwa
**New**: https://github.com/abbyluggery/divergentthrive-pwa

**Impact**:
- GitHub Pages URL will change
- OAuth callback URL must update in Salesforce
- All git remotes must update

**New GitHub Pages URL**: https://abbyluggery.github.io/divergentthrive-pwa/

### 4. Salesforce OAuth Connected App
**Current Callback URL**: https://abbyluggery.github.io/neurothrive-pwa/oauth/callback
**New Callback URL**: https://abbyluggery.github.io/divergentthrive-pwa/oauth/callback

**Action Required**: Update in Salesforce Connected App settings

---

## 🔄 Renaming Execution Plan

### Option A: Manual Renaming (Recommended for Safety)

**Step 1: Backup**
```bash
# Backup PWA
cd "C:\Users\Abbyl\OneDrive\Desktop"
cp -r neurothrive-pwa-standalone divergentthrive-pwa-backup

# Backup SafeHaven-Build
cd "C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training"
cp -r SafeHaven-Build SafeHaven-Build-backup
```

**Step 2: Rename PWA Repository**
1. Go to GitHub: https://github.com/abbyluggery/neurothrive-pwa/settings
2. Repository name: `neurothrive-pwa` → `divergentthrive-pwa`
3. Click "Rename"
4. Update local git remote:
   ```bash
   cd "C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone"
   git remote set-url origin https://github.com/abbyluggery/divergentthrive-pwa.git
   ```

**Step 3: Update Critical PWA Files**
1. Edit [manifest.json](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\manifest.json) - Change name, short_name, description
2. Edit [index.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\index.html) - Find/replace "NeuroThrive" → "DivergentThrive"
3. Edit [sw.js](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\sw.js) - Update CACHE_NAME
4. Edit [secret-unlock-template.html](C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone\secret-unlock-template.html) - Update deep link URL

**Step 4: Update Documentation**
- Use VS Code Find/Replace across all files:
  - Find: `NeuroThrive`
  - Replace: `DivergentThrive`
  - Files to include: `*.md, *.html, *.js`

**Step 5: Test Locally**
```bash
cd "C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone"
python -m http.server 8080
# Visit http://localhost:8080
# Verify all "DivergentThrive" references appear correctly
```

**Step 6: Commit and Push**
```bash
git add .
git commit -m "refactor: rename project from NeuroThrive to DivergentThrive

- Update app name in manifest.json
- Update all UI references in index.html
- Update service worker cache name
- Update deep link scheme to divergentthrive://
- Update all documentation references

Reason: NeuroThrive is a trademarked name"

git push origin main
```

**Step 7: Update GitHub Pages**
1. Go to: https://github.com/abbyluggery/divergentthrive-pwa/settings/pages
2. Verify still enabled for `main` branch
3. New URL: https://abbyluggery.github.io/divergentthrive-pwa/

**Step 8: Update Salesforce**
1. Go to Salesforce Connected App settings
2. Update callback URL: https://abbyluggery.github.io/divergentthrive-pwa/oauth/callback
3. Save

**Step 9: Update SafeHaven-Build Documentation**
```bash
cd "C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build"
# Use VS Code Find/Replace in all .md files:
# Find: NeuroThrive
# Replace: DivergentThrive

git add *.md
git commit -m "docs: update project name to DivergentThrive"
git push origin main
```

---

### Option B: Automated Script (Faster but Riskier)

**NOTE**: Only use if comfortable with automated replacements. Test with backup first.

**Script** (PowerShell):
```powershell
# WARNING: TEST ON BACKUP FIRST

$PWA_PATH = "C:\Users\Abbyl\OneDrive\Desktop\neurothrive-pwa-standalone"
$SAFEHAVEN_PATH = "C:\Users\Abbyl\OneDrive\Desktop\Salesforce Training\SafeHaven-Build"

# Function to replace text in file
function Replace-InFile {
    param($FilePath, $Find, $Replace)
    (Get-Content $FilePath -Raw) -replace $Find, $Replace | Set-Content $FilePath
}

# PWA Critical Files
Replace-InFile "$PWA_PATH\manifest.json" "NeuroThrive" "DivergentThrive"
Replace-InFile "$PWA_PATH\index.html" "NeuroThrive" "DivergentThrive"
Replace-InFile "$PWA_PATH\sw.js" "neurothrive" "divergentthrive"
Replace-InFile "$PWA_PATH\secret-unlock-template.html" "neurothrive://" "divergentthrive://"
Replace-InFile "$PWA_PATH\secret-unlock-template.html" "app.neurothrive." "app.divergentthrive."

# Bulk documentation update
Get-ChildItem -Path $PWA_PATH -Filter *.md -Recurse | ForEach-Object {
    Replace-InFile $_.FullName "NeuroThrive" "DivergentThrive"
    Replace-InFile $_.FullName "neurothrive" "divergentthrive"
}

Get-ChildItem -Path $SAFEHAVEN_PATH -Filter *.md | ForEach-Object {
    Replace-InFile $_.FullName "NeuroThrive" "DivergentThrive"
    Replace-InFile $_.FullName "neurothrive-pwa" "divergentthrive-pwa"
}

Write-Host "Renaming complete! Review changes before committing."
```

---

## ⚠️ Post-Renaming Verification Checklist

### PWA Verification
- [ ] App name shows "DivergentThrive" when installed
- [ ] Browser tab title shows "DivergentThrive"
- [ ] All UI text updated correctly
- [ ] Service worker registers with new cache name
- [ ] OAuth flow works with new callback URL
- [ ] Deep link uses `divergentthrive://`

### GitHub Verification
- [ ] Repository renamed successfully
- [ ] GitHub Pages URL works: https://abbyluggery.github.io/divergentthrive-pwa/
- [ ] All commits preserved
- [ ] Local git remote updated

### Salesforce Verification
- [ ] Connected App callback URL updated
- [ ] OAuth flow completes successfully
- [ ] Data sync still works

### Documentation Verification
- [ ] All README files updated
- [ ] Integration guides reference correct names
- [ ] No broken links to old repository name

---

## 📊 Impact Assessment

### Breaking Changes
1. **Deep Link Scheme**: `neurothrive://` → `divergentthrive://`
   - Impact: Existing secret unlock won't work until Android app updated
   - Mitigation: Update both PWA and Android simultaneously in Day 3

2. **GitHub Pages URL**: Domain change
   - Impact: Old URL will redirect, but bookmarks/links break
   - Mitigation: GitHub provides automatic redirect

3. **OAuth Callback**: URL change required
   - Impact: Login broken until Salesforce updated
   - Mitigation: Update Connected App immediately after rename

### Non-Breaking Changes
1. Package names in Android (keeping `app.neurothrive.safehaven` temporarily)
2. Salesforce object names (no rename needed)
3. Local file paths (can remain as is)

---

## 🎯 Recommended Timeline

**Immediate** (Today):
1. Create backup of both repositories
2. Rename GitHub repository
3. Update critical PWA files (manifest, index.html, sw.js, secret-unlock)
4. Update Salesforce OAuth callback
5. Test locally, commit, push
6. Verify GitHub Pages deployment

**After SafeHaven Replacement Name Chosen**:
1. Update Android package names
2. Update deep link host
3. Rename Android app in Play Store
4. Update all remaining documentation

---

## 📞 Support Information

**Repositories**:
- Current: https://github.com/abbyluggery/neurothrive-pwa
- New: https://github.com/abbyluggery/divergentthrive-pwa (after rename)

**Related Documents**:
- [OPTION1_IMPLEMENTATION_STATUS.md](./OPTION1_IMPLEMENTATION_STATUS.md) - Day 1-5 implementation plan
- [COMPLETE_INTEGRATION_STRATEGY.md](./COMPLETE_INTEGRATION_STRATEGY.md) - Overall integration approach
- [CORRECTED_SALESFORCE_STATUS.md](./CORRECTED_SALESFORCE_STATUS.md) - Salesforce platform status

**Questions**:
1. Should we rename the GitHub repository immediately, or wait until Option 1 Day 2 (secret unlock integration) is complete?
2. For Android package name, use `app.divergentthrive.safehaven` or wait for SafeHaven replacement?
3. Should we update Salesforce custom object references, or keep as "NeuroThrive" internally?

---

**READY TO EXECUTE**: All files identified, changes documented, execution plan provided. Awaiting user confirmation to proceed with renaming.
