# SafeHaven - Free Domestic Violence Safety Planning Platform

**Mission**: Provide free, encrypted, intersectionally-informed safety planning tools for domestic violence survivors.

**Status**: Production Ready for 24-Hour Sprint
**Platforms**: Android (Kotlin), Salesforce (Apex), Web (React)
**License**: MIT (Free & Open Source)

---

## 📋 Quick Links

- **[Executive Summary](SafeHaven%20Executive%20Summary.md)** - 5-minute overview
- **[Technical Specifications](SafeHaven%20Technical%20Specification.MD)** - Complete architecture
- **[Quick Start Guide](SafeHaven%20Quick%20Start%20Guide%20-%2024.md)** - 24-hour sprint guide
- **[Database Schema](# SafeHaven Database Schema (Room).md)** - Room entities and DAOs
- **[Complete Documentation](NeuroThrive%20SafeHaven%20-%20Complete%20Documentation.md)** - Full project docs

### 🆕 New Resources
- **[Abuse Resources & Self-Help Guide](SafeHaven%20Abuse%20Resources%20and%20Self-Help%20Guide.md)** - Comprehensive guide on abuse types, emotional abuse identification, DARVO tactics, and court documentation strategies
- **[Enhanced Emergency Alert System](SafeHaven%20Enhanced%20Emergency%20Alert%20System.md)** - SOS panic button specifications, emergency alerts, and offline capabilities
- **[Ecosystem & Web Presence Strategy](SafeHaven%20Ecosystem%20and%20Web%20Presence%20Strategy.md)** - Web platform, BestyBnB (pet safety), and LadyDriver (women's ride-share) specifications

---

## 🎯 What You're Building

A **free, encrypted safety planning app** for domestic violence survivors that:

1. **Silent Documentation** - Camera with no sound, flash, or GPS metadata
2. **Document Verification** - SHA-256 + blockchain timestamping
3. **Intersectional Resources** - 1,000+ orgs filtered by LGBTQIA+, BIPOC, male, undocumented, disabled identities
4. **Panic Features** - Quick delete all data (shake phone), dual passwords
5. **Economic Independence** - Integrated job search platform

---

## 🚀 24-Hour Sprint Priority

### CRITICAL (Hours 0-12) ✅

1. **Database Schema** (Hours 0-2)
   - SafeHavenProfile, IncidentReport, VerifiedDocument, EvidenceItem, LegalResource

2. **Encryption System** (Hours 2-4)
   - AES-256-GCM with Android KeyStore
   - File encryption for photos/videos

3. **Silent Camera** (Hours 4-8)
   - Mute system volume during capture
   - Remove GPS metadata
   - Immediate encryption

4. **Panic Delete** (Hours 8-10)
   - Shake detection (3 rapid shakes)
   - Secure file deletion (<2 seconds)

5. **Document Verification** (Hours 10-12)
   - SHA-256 hashing
   - Blockchain timestamping (Polygon)

### IMPORTANT (Hours 12-18) ⚠️

6. **Intersectional Resource Matching**
7. **Incident Report Form**
8. **Evidence Vault**

### NICE-TO-HAVE (Hours 18-24) 💡

9. **Onboarding Flow**
10. **Settings Screen**
11. **Salesforce Sync**

---

## 🛠️ Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Mobile** | Kotlin + Jetpack Compose | Native Android app |
| **Database** | Room (SQLite) | Local encrypted storage |
| **Encryption** | AES-256-GCM + Android KeyStore | Zero-knowledge encryption |
| **Backend** | Salesforce (Apex) | Cloud sync, resource database |
| **Cloud Storage** | AWS S3 | Encrypted evidence backup |
| **Blockchain** | Polygon (Web3j) | Document timestamping |
| **PDF** | iText7 | Verified document generation |
| **Camera** | CameraX | Silent photo/video capture |

---

## 👥 Who This Serves

### Marginalized Survivors We Center

- **Trans survivors** (especially trans women of color)
- **BIPOC survivors** (Black, Latina, Asian/Pacific Islander, Indigenous)
- **Male-identifying survivors** (very few resources exist)
- **Undocumented survivors** (U-Visa support, no ICE contact)
- **Disabled survivors** (accessibility, interpreter access)
- **Deaf/DeafBlind survivors** (ASL resources)

**Why Intersectionality Matters**: A trans Black woman needs different resources than a cisgender white woman. Our algorithm prioritizes identity-specific matches.

---

## 📊 Key Statistics

- **70% of survivors** cannot leave due to economic dependence
- **54% of trans people** experience IPV (highest rate)
- **44% of lesbian women** experience IPV
- **1 in 4 men** experience IPV (often underreported)
- **85% of survivors** report tech-facilitated abuse (spyware, GPS tracking)

---

## 🔐 Security Architecture

### Triple-Layer Encryption
1. **Database Level**: SQLCipher (AES-256)
2. **Field Level**: Sensitive fields double-encrypted
3. **File Level**: Evidence files encrypted before storage

### Zero-Knowledge Design
- All encryption keys in Android KeyStore (hardware-backed)
- No plaintext data ever sent to server
- Server only receives encrypted blobs

### Document Verification
- SHA-256 hash of document photo
- Blockchain timestamping (Polygon - $0.001/tx)
- Web verification portal

---

## 📱 Core Features

### 1. Silent Documentation System
- ❌ No shutter sound
- ❌ No flash (default off)
- ❌ No gallery thumbnails
- ❌ No GPS metadata
- ✅ Immediate AES-256 encryption

### 2. Document Verification
```
Document Photo → SHA-256 Hash → Polygon Blockchain → Timestamped Hash
                                  ↓
                          Verified PDF with QR Code
```

**Use Cases**:
- Court evidence: "This document was photographed on [date], verified by blockchain"
- Open bank account without original birth certificate
- Prove ID for new apartment

### 3. Intersectional Resource Matching

**Scoring Algorithm**:
```
Score = Base(10) + Identity Matches + Distance Penalty

Identity Bonuses:
- Trans-specific: +30 points
- Undocumented-friendly: +30 points
- Male survivor services: +25 points
- LGBTQIA+ inclusive: +20 points
- BIPOC-led: +20 points
```

### 4. Panic Features

**Quick Delete** (Shake phone 3x):
- Delete all data
- Overwrite with random data
- <2 second execution time

**Dual Password**:
- Real password → actual evidence
- Decoy password → fake/minimal data

**SOS Mode** (Hold panic button 3 seconds):
- Enable GPS
- Send location to emergency contacts
- Start silent audio recording

### 5. Economic Independence
- AI-powered job search (from NeuroThrive platform)
- Resume generation
- Interview prep
- Opportunity tracking

---

## 📦 Repository Contents

```
Safehaven-documentation/
├── README.md (this file)
├── SafeHaven Executive Summary.md
├── SafeHaven Technical Specification.MD
├── SafeHaven Quick Start Guide - 24.md
├── SafeHaven Database Schema (Room).md
├── NeuroThrive SafeHaven - Complete Documentation.md
├── SafeHaven Abuse Resources and Self-Help Guide.md (NEW)
├── SafeHaven Enhanced Emergency Alert System.md (NEW)
├── SafeHaven Ecosystem and Web Presence Strategy.md (NEW)
├── Create GitHub Repository.md
└── INSTRUCTION FOR CLAUDE CODE.md
```

---

## 🚦 Getting Started

### Prerequisites
- Android Studio (latest)
- Salesforce Developer Edition org
- Node.js 18+ (for web portal)
- Git

### Quick Setup (15 minutes)

1. **Review Documentation**
   - Read [Executive Summary](SafeHaven%20Executive%20Summary.md) (5 min)
   - Review [Technical Specs](SafeHaven%20Technical%20Specification.MD) (10 min)

2. **Follow Sprint Guide**
   - [24-Hour Quick Start Guide](SafeHaven%20Quick%20Start%20Guide%20-%2024.md)
   - Start with Database Schema (Hours 0-2)

3. **Build Android App**
   - Use [Database Schema](# SafeHaven Database Schema (Room).md) for Room entities
   - Follow code templates in Technical Specifications

---

## 🎯 Success Metrics

### Safety
- **0 data breaches** (zero-knowledge design)
- **<2 second panic delete**
- **100% encryption coverage**

### User Adoption
- **1,000 active users** in 6 months
- **10,000 incident reports** created
- **500 verified documents** timestamped

### Resource Matching
- **90%+ satisfaction** with recommendations
- **<5 mile average distance** to top resource
- **70%+ identity match rate**

### Economic Impact
- **500 job applications** via platform
- **100 interviews** scheduled
- **50 job offers** accepted
- **$500K+ total salary** of jobs accepted

---

## 🗺️ Roadmap

### Phase 1: MVP (Q1 2026)
- ✅ Silent camera
- ✅ Encrypted evidence vault
- ✅ Incident reports
- ✅ Document verification
- ✅ Panic delete
- ✅ Resource finder

### Phase 2: Economic Independence (Q2 2026)
- ✅ Job search integration
- ✅ Resume builder
- ✅ Interview prep
- ⚠️ Financial planning

### Phase 3: Community (Q3 2026)
- ⚠️ Anonymous peer support (encrypted)
- ⚠️ Verified survivor stories
- ⚠️ Safety planning templates

### Phase 4: Professional Tools (Q4 2026)
- ⚠️ Case manager portal
- ⚠️ Court-ready evidence exports
- ⚠️ Multi-survivor household support

---

## 🤝 Contributing

### Developers
- Build Android app (see Quick Start)
- Improve encryption/security
- Add internationalization (Spanish, ASL videos)

### Designers
- Create trauma-informed UI/UX
- Design accessibility features
- Build brand identity (stealth mode)

### Researchers
- Validate resource matching algorithm
- Survivor focus groups
- Academic partnerships

### Advocates
- Verify legal resource database
- Pilot with DV organizations
- Feedback on safety features

---

## ⚖️ Legal & Privacy

### Data Collection
- **On Device**: All user data (encrypted)
- **On Salesforce**: Encrypted incident reports (opt-in)
- **On Blockchain**: Document hashes only (no personal data)
- **On AWS S3**: Encrypted evidence backups (opt-in)

### What We DON'T Collect
- ❌ GPS location (unless explicitly enabled)
- ❌ Unencrypted photos/videos
- ❌ Passwords (hashed only)
- ❌ Biometric data
- ❌ Analytics/tracking data

### GDPR/CCPA Compliance
- Right to deletion: Panic delete feature
- Data portability: Export to PDF
- No tracking/analytics
- No third-party SDKs (except AWS, Polygon)

---

## ⚠️ Disclaimer

**This is NOT a substitute for professional DV services.**

**If you are in immediate danger:**
- **Call 911** (US)
- **National DV Hotline**: 1-800-799-7233
- **Crisis Text Line**: Text START to 88788

SafeHaven is a documentation and planning tool. Please work with local DV organizations for comprehensive support.

---

## 📜 License

MIT License - Free and open source

**Why MIT?** We want this tech used by as many survivors as possible. Other DV orgs, please fork and build your own version!

---

## 🙏 Acknowledgments

### Research Foundation
- Dr. Evan Stark (coercive control framework)
- Dr. Kimberlé Crenshaw (intersectionality)
- The Network/La Red (LGBTQ+ IPV research)
- APIAHF, Casa de Esperanza, IDVAAC (BIPOC research)

### Inspiration
- **Aspire News** (early DV safety app - discontinued)
- **MyPlan** (Johns Hopkins safety planning app)
- **Circle of 6** (peer-based safety app)

### Built With Love By
- Abby Luggery (Developer, DV survivor advocate)
- Powered by NeuroThrive platform

---

## 📞 Contact

- **GitHub Issues**: Report bugs, request features
- **Email**: safehaven@neurothrive.app (coming soon)

---

**Last Updated**: November 17, 2025
**Version**: 1.0.0
**Status**: Production Ready for 24-Hour Sprint

---

## 🔗 Related Projects

- [NeuroThrive Complete Feature List](https://github.com/abbyluggery/neurothrive-documentation/COMPLETE_FEATURE_LIST_ALL_PLATFORMS.md)
- NeuroThrive Android App
- NeuroThrive PWA
- NeuroThrive Salesforce Platform

---

**Ready to build? Start with the [24-Hour Sprint Guide](SafeHaven%20Quick%20Start%20Guide%20-%2024.md)!**