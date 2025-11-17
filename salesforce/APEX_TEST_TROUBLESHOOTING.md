# Apex Test Classes Troubleshooting Guide

**Status**: 2 test classes reported as failing
**Total Test Classes**: 4 (SafeHavenSyncAPITest, DocumentVerificationAPITest, ResourceMatchingAPITest, PanicDeleteAPITest)
**Total Test Methods**: 32

---

## Common Salesforce Test Failures & Fixes

### Issue 1: Missing Required Fields

**Symptoms**: `REQUIRED_FIELD_MISSING` error

**Likely Culprits**:
- Custom objects may have required fields not populated in test data
- Lookup relationships might be missing

**Fix**:
```apex
// Ensure all required fields are populated in @testSetup
SafeHaven_Profile__c profile = new SafeHaven_Profile__c(
    User_ID__c = 'test_user_123',  // External ID - REQUIRED
    Password_Hash__c = 'hash',      // REQUIRED
    Duress_Password_Hash__c = 'duress', // REQUIRED
    Created_Timestamp__c = DateTime.now(), // REQUIRED
    Last_Modified_Timestamp__c = DateTime.now() // REQUIRED
);
```

### Issue 2: External ID Uniqueness

**Symptoms**: `DUPLICATE_VALUE` error

**Problem**: Test classes may create duplicate External IDs (User_ID__c, SHA256_Hash__c)

**Fix**: Use unique values per test method
```apex
String uniqueUserId = 'test_user_' + System.currentTimeMillis();
String uniqueHash = 'hash_' + System.currentTimeMillis() + '_' + Math.random();
```

### Issue 3: RestContext Not Set

**Symptoms**: `System.NullPointerException: Attempt to de-reference a null object`

**Problem**: RestContext.request or RestContext.response is null

**Fix**: Ensure RestContext is set before calling API
```apex
RestRequest req = new RestRequest();
req.requestURI = '/services/apexrest/safehaven/v1/sync/profile';
req.httpMethod = 'POST';
req.requestBody = Blob.valueOf(JSON.serialize(requestBody));

RestContext.request = req;  // CRITICAL - must set before API call

Test.startTest();
SafeHavenSyncAPI.SyncResponse response = SafeHavenSyncAPI.syncProfile();
Test.stopTest();
```

### Issue 4: SOQL/DML Governor Limits

**Symptoms**: `System.LimitException: Too many SOQL queries`

**Problem**: Tests hitting governor limits

**Fix**: Bulkify test data creation
```apex
@testSetup
static void setupTestData() {
    List<SafeHaven_Profile__c> profiles = new List<SafeHaven_Profile__c>();
    // Create all test data in bulk
    insert profiles;
}
```

### Issue 5: Field-Level Security

**Symptoms**: `FIELD_INTEGRITY_EXCEPTION` or fields not accessible

**Problem**: Running user doesn't have field-level security

**Fix**: Run as System Admin or use `System.runAs()`
```apex
@isTest
static void testWithSystemAdmin() {
    User testUser = [SELECT Id FROM User WHERE Profile.Name = 'System Administrator' LIMIT 1];
    System.runAs(testUser) {
        // Test code here
    }
}
```

---

## Specific Test Class Issues

### SafeHavenSyncAPITest

**Potential Issues**:
1. `testIncidentSync()` - Missing required Incident_Report__c fields
2. `testEvidenceSync()` - SHA256_Hash__c must be unique (64 characters)
3. `testSurvivorProfileSync()` - User_ID__c external ID uniqueness

**Recommended Fixes**:

```apex
// Fix for testIncidentSync
Incident_Report__c incident = new Incident_Report__c(
    User_ID__c = 'test_user_123',
    Incident_Type__c = 'physical',
    Incident_Timestamp__c = DateTime.now(),
    Description_Encrypted__c = 'encrypted',
    Created_Timestamp__c = DateTime.now(),
    Last_Modified_Timestamp__c = DateTime.now() // ADD THIS
);
```

### DocumentVerificationAPITest

**Potential Issues**:
1. SHA256 hash must be exactly 64 characters
2. Duplicate hash checks might fail

**Recommended Fixes**:

```apex
// Ensure hash is exactly 64 characters (SHA-256 format)
String validHash = 'abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567890'; // 64 chars

// For duplicate test, use different hash
String uniqueHash = 'duplicate_hash_' + String.valueOf(System.currentTimeMillis()).leftPad(48, '0');
```

### ResourceMatchingAPITest

**Potential Issues**:
1. Legal_Resource__c might be missing required fields
2. Survivor_Profile__c might not link correctly

**Recommended Fixes**:

```apex
// Add required timestamp fields
Legal_Resource__c resource = new Legal_Resource__c(
    Name = 'Test Resource',
    Resource_Type__c = 'shelter',
    Organization_Name__c = 'Test Org',
    City__c = 'San Francisco',
    State__c = 'CA',
    Latitude__c = 37.7749,
    Longitude__c = -122.4194,
    Verified__c = true,
    Created_Timestamp__c = DateTime.now(),
    Last_Modified_Timestamp__c = DateTime.now()
);
```

### PanicDeleteAPITest

**Potential Issues**:
1. Cascade deletes might fail if lookup relationships aren't set
2. Related records might not delete properly

**Recommended Fixes**:

```apex
// Ensure lookups are set correctly
Incident_Report__c incident = new Incident_Report__c(
    User_ID__c = 'panic_test_user',
    SafeHaven_Profile__c = profile.Id,  // ADD lookup if exists
    // ... other fields
);
```

---

## Quick Diagnostic Steps

### Step 1: Run All Tests
```bash
sf apex run test --test-level RunLocalTests --result-format human --code-coverage --wait 10
```

### Step 2: Check Specific Test Class
```bash
sf apex run test --class-names SafeHavenSyncAPITest --result-format human --code-coverage
```

### Step 3: View Detailed Error Log
```bash
sf apex get test --test-run-id <TEST_RUN_ID> --result-format human
```

### Step 4: Check Debug Logs
```bash
sf apex log get --number 1
```

---

## Most Likely Issues (Based on Code Review)

### 1. Missing Last_Modified_Timestamp__c
Many custom objects have this as a required field but test data might not populate it.

**Global Fix**: Add to all test data inserts
```apex
Last_Modified_Timestamp__c = DateTime.now()
```

### 2. SHA256 Hash Length
All hashes must be exactly 64 characters (SHA-256 standard).

**Global Fix**: Use this helper
```apex
private static String generateValidSHA256() {
    return EncodingUtil.convertToHex(
        Crypto.generateDigest('SHA-256', Blob.valueOf(String.valueOf(Math.random())))
    );
}
```

### 3. External ID Uniqueness
User_ID__c, SHA256_Hash__c are marked as unique external IDs.

**Global Fix**: Add timestamp to ensure uniqueness
```apex
'test_user_' + System.currentTimeMillis()
```

---

## Running Tests After Fixes

```bash
# Deploy with test execution
sf project deploy start --source-dir salesforce/force-app/main/default/classes --test-level RunLocalTests

# Or run tests only (after deployment)
sf apex run test --test-level RunLocalTests --code-coverage --wait 10
```

---

## Expected Outcome

✅ All 32 test methods should pass
✅ Code coverage should be 75%+
✅ No compilation errors
✅ No runtime errors

---

**Last Updated**: November 17, 2025
**Status**: Diagnostic guide - ready for debugging
