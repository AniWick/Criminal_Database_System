# Criminal Management System

A comprehensive Java-based application for managing criminal records, cases, evidence, and biometric data with advanced features like role-based access control, wanted list management, and real-time notifications.

## 🎯 Overview

The Criminal Management System is built using **Object-Oriented Analysis and Design (OOAD)** principles with multiple design patterns including MVC, Facade, Strategy, Builder, Observer, and State patterns. The system provides law enforcement agencies with tools to efficiently manage criminal databases, case investigations, and evidence tracking.

---

## 📋 Features Implemented

### Core Features (Original)
- ✅ **User Authentication** - Login/logout with role-based access
- ✅ **Criminal Record Management** - Add, update, delete, and search criminal records
- ✅ **Case Management** - Create cases, assign to officers, track case details
- ✅ **Evidence Management** - Store and manage evidence related to cases
- ✅ **Biometric Data** - Store and retrieve fingerprint and DNA data

### Advanced Features (6 New Features)

#### 1️⃣ **Advanced Search** (Strategy Pattern)
- Multi-criteria filtering by name, age range, and crime type
- Composable search strategies for flexible querying
- **Tab:** Advanced Search

#### 2️⃣ **Role-Based Access Control** (Strategy Pattern)
- 4 Role Levels: ADMIN, DETECTIVE, OFFICER, ANALYST
- Fine-grained permission system with 14+ operations
- Dynamic permission checking based on user role
- **Roles:**
  - **ADMIN**: Full system access
  - **DETECTIVE**: Investigation and case management
  - **OFFICER**: Limited add/search operations
  - **ANALYST**: View-only access and reporting

#### 3️⃣ **Wanted List & Alert System** (Observer Pattern)
- Flag criminals as "WANTED" with severity levels (HIGH, MEDIUM, LOW)
- Real-time alert notifications when wanted criminals are involved in new activities
- Searchable wanted criminal database
- **Tab:** Wanted List

#### 4️⃣ **Evidence Chain of Custody** (Observer Pattern)
- Track evidence handling from collection to testing
- Record who handled evidence, when, where, and what action was taken
- Maintain complete audit trail for legal compliance
- **Tab:** Chain of Custody

#### 5️⃣ **Notification System** (Observer Pattern)
- Real-time notifications for case assignments
- Evidence update alerts
- Wanted criminal spotting notifications
- Mark notifications as read/unread
- **Tab:** Notifications

#### 6️⃣ **Report Generation** (Builder Pattern)
- Criminal statistics reports with crime breakdowns
- Case summary reports with assignment status
- Evidence inventory reports
- Customizable report generation with builder pattern
- **Tab:** Reports

---

## 🏗️ Architecture & Design Patterns

```
View (GUI) 
    ↓
Facade (CriminalManagementFacade)
    ↓
Controllers (CriminalController, CaseController, etc.)
    ↓
Services (CriminalService, CaseService, AlertService, etc.)
    ↓
Repository/Database (InMemoryCriminalRepository, CriminalDatabase)
```

### Design Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| **MVC** | view/, controller/, service/ | Separation of concerns |
| **Facade** | facade/ | Unified interface to subsystems |
| **Strategy** | strategy/ | Flexible algorithm selection for searching |
| **Factory** | model/ModelFactory.java | Object creation |
| **Builder** | service/ReportBuilder.java | Complex report generation |
| **Observer** | service/AlertService, NotificationService | Event-driven notifications |
| **Singleton** | app/AppContext.java | Single instance management |

---

## 💻 System Requirements

- **Java Version:** Java 11 or higher (tested with Java 22)
- **Operating System:** Windows, macOS, or Linux
- **Memory:** Minimum 512MB RAM
- **Storage:** ~10MB for compiled files and data

---

## 📂 Project Structure

```
criminal-management-system/
├── app/
│   └── AppContext.java                 # Application initialization
├── controller/
│   ├── BiometricController.java
│   ├── CaseController.java
│   ├── CriminalController.java
│   ├── EvidenceController.java
│   └── LoginController.java
├── database/
│   └── CriminalDatabase.java           # Persistent data storage
├── facade/
│   ├── CriminalManagement.java
│   └── CriminalManagementFacade.java
├── model/
│   ├── BiometricData.java
│   ├── CaseRecord.java
│   ├── ChainOfCustody.java             # NEW
│   ├── Criminal.java
│   ├── Evidence.java
│   ├── Notification.java               # NEW
│   ├── Report.java                     # NEW
│   ├── Role.java                       # NEW
│   ├── User.java
│   ├── WantedNotice.java               # NEW
│   └── ModelFactory.java
├── repository/
│   ├── CriminalRepository.java
│   └── InMemoryCriminalRepository.java
├── service/
│   ├── AlertService.java               # NEW
│   ├── AuthService.java
│   ├── BiometricService.java
│   ├── CaseService.java
│   ├── CriminalService.java
│   ├── EvidenceChainService.java       # NEW
│   ├── EvidenceService.java
│   ├── NotificationService.java        # NEW
│   ├── PermissionService.java          # NEW
│   └── ReportBuilder.java              # NEW
├── strategy/
│   ├── AgeRangeSearchStrategy.java     # NEW
│   ├── AdvancedSearchStrategy.java     # NEW
│   ├── CriminalIdSearchStrategy.java
│   ├── CriminalSearchStrategy.java
│   └── CrimeTypeSearchStrategy.java    # NEW
├── validation/
│   └── CriminalValidator.java
├── view/
│   ├── MainUI.java                     # Console UI (original)
│   └── ModernCriminalManagementGUI.java # NEW - Swing GUI
├── FeatureTest.java                    # Test file for 6 features
├── GUIIntegrationTest.java             # Integration test with scenarios
└── data/
    └── criminal-management-db.ser      # Serialized database file
```

---

## 🚀 Getting Started

### Compilation

Compile all Java files:
```bash
cd criminal-management-system
javac -d . $(find . -name "*.java")
```

Or compile specific modules:
```bash
# Compile services
javac -d . service/*.java

# Compile models
javac -d . model/*.java

# Compile strategies
javac -d . strategy/*.java

# Compile GUI
javac -d . view/ModernCriminalManagementGUI.java
```

### Running the Application

#### Option 1: Modern GUI (Recommended)
```bash
java view.ModernCriminalManagementGUI
```

#### Option 2: Console UI
```bash
java view.MainUI
```

#### Option 3: Run Tests
```bash
# Test all 6 features
java FeatureTest

# Integration test with realistic scenarios
java GUIIntegrationTest
```

---

## 🔐 Login Credentials

### Demo Accounts
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| detective | detect123 | DETECTIVE |

---

## 📖 Usage Guide

### 1. Login
- Enter username and password from demo credentials
- System validates and loads appropriate interface based on role

### 2. Criminal Management
- **Add Criminal:** Dashboard → Criminals → Add Criminal
- **Search:** Multiple options:
  - Simple Search: ID-based search
  - Advanced Search: Multi-criteria (name, age range, crime type)
- **Update:** Edit crime type for existing criminals
- **Delete:** Remove criminal records (Admin only)

### 3. Case Management
- **Create Case:** Link case to criminal ID
- **Assign Case:** Assign investigation to specific officer
- **View Cases:** See all open/closed cases with assignment status

### 4. Evidence Management
- **Add Evidence:** Link physical/digital evidence to cases
- **Chain of Custody:** Track evidence handling from collection to testing
- **View Evidence:** Browse evidence by case

### 5. Biometric Data
- **Store:** Fingerprint and DNA data
- **Retrieve:** Look up biometric records by criminal ID
- **Compare:** Future feature for biometric matching

### 6. Wanted List
- **Add to Wanted:** Flag high-priority criminals
- **Set Severity:** HIGH/MEDIUM/LOW priority levels
- **Trigger Alerts:** Get notifications when wanted criminals are spotted

### 7. Notifications
- **Real-time Updates:** Receive alerts for case assignments
- **Evidence Updates:** Track changes to evidence
- **Wanted Criminal Alerts:** Instant notification for wanted individual sightings

### 8. Reports
- **Criminal Statistics:** Crime type breakdown, average age, total count
- **Case Summary:** Case distribution, assignment status, details
- **Evidence Inventory:** Evidence type distribution

---

## 🧪 Testing

### Running Feature Tests

All 6 features have been tested with both unit tests and integration tests:

```bash
# Test all features individually
java FeatureTest

# Test features with realistic scenarios
java GUIIntegrationTest
```

### Test Coverage
- ✅ Advanced Search (Multi-criteria filtering)
- ✅ Role-Based Access Control (4 role types)
- ✅ Wanted List & Alerts (Observer pattern)
- ✅ Evidence Chain of Custody (Tracking system)
- ✅ Notifications (Real-time alerts)
- ✅ Report Generation (Builder pattern)

---

## 📊 Data Persistence

The system uses **serialization** to persist data:
- **File:** `data/criminal-management-db.ser`
- **Format:** Java Object Serialization
- **Scope:** All criminals, cases, evidence, biometric data, and audit logs

Data is automatically saved after modifications and loaded on startup.

---

## 🔍 Design Pattern Details

### 1. Strategy Pattern (Advanced Search)
- Multiple search strategies (by name, age, crime type)
- Easy to add new search criteria
- Runtime algorithm selection

### 2. Observer Pattern (Alerts & Notifications)
- AlertService notifies listeners of wanted criminal additions
- NotificationService broadcasts to interested recipients
- Decoupled event producers and consumers

### 3. Builder Pattern (Reports)
- Fluent API for report generation
- Complex report construction made simple
- `new ReportBuilder(1, "admin").withCriminalStats(criminals).build()`

### 4. Facade Pattern
- CriminalManagementFacade provides unified interface
- Hides complex subsystem interactions
- Single entry point for UI

### 5. MVC Architecture
- **Model:** Criminal, Case, Evidence classes
- **View:** ModernCriminalManagementGUI (Swing)
- **Controller:** *Controller classes handle business logic
- **Service:** Business operations
- **Repository:** Data access layer

---

## 📝 Example Workflows

### Workflow 1: Registering a Wanted Criminal
1. Officer logs in with DETECTIVE role
2. Navigate to Wanted List tab
3. Add Criminal ID, reason, and severity
4. System broadcasts "WANTED_ADDED" alert
5. All connected officers receive notification

### Workflow 2: Tracking Evidence
1. Evidence collected at crime scene
2. Officer records: COLLECTED by Officer Smith at Crime Scene
3. Detective transfers: TRANSFERRED to Evidence Room
4. Lab Tech tests: TESTED with DNA analysis results
5. Complete chain of custody available for legal proceedings

### Workflow 3: Generating Statistics
1. Superintendent requests Criminal Statistics
2. System aggregates criminal data
3. Report includes: Total count, crime type breakdown, average age
4. Report generated with timestamp and officer name

---

## 🛠️ Troubleshooting

### Issue: "Class not found" error
**Solution:** Ensure all files are compiled to the same output directory
```bash
javac -d . **/*.java
```

### Issue: GUI doesn't start
**Solution:** Verify Java version supports Swing
```bash
java -version  # Should be Java 11+
```

### Issue: Data not persisting
**Solution:** Ensure write permissions in `data/` directory

### Issue: .toList() error on Java 11
**Solution:** Already fixed! Uses `Collectors.toList()` for compatibility

---

## 🎓 Learning Resources

### Design Patterns in Code
- **Strategy:** `strategy/NameSearchStrategy.java`
- **Observer:** `service/AlertService.java`, `NotificationService.java`
- **Builder:** `service/ReportBuilder.java`
- **Facade:** `facade/CriminalManagementFacade.java`

### Test Files
- `FeatureTest.java` - Individual feature testing
- `GUIIntegrationTest.java` - Realistic scenario testing

---

## 📞 Support & Contributions

### Key Features Ready for Extension
1. Biometric matching algorithm
2. Data export (PDF, CSV, JSON)
3. Multi-user concurrency handling
4. Advanced analytics and dashboards
5. Web API layer
6. Database migration (from serialization to SQL)

---

## 📄 License

This project is part of an Object-Oriented Analysis and Design (OOAD) curriculum.

---

## ✨ Summary

The Criminal Management System is a feature-rich application demonstrating:
- ✅ Professional GUI with Swing
- ✅ Multiple design patterns
- ✅ Role-based security
- ✅ Real-time notifications
- ✅ Comprehensive testing
- ✅ Clean architecture
- ✅ Data persistence

**All 6 advanced features are fully implemented, tested, and integrated into the GUI!**

---

**Last Updated:** April 16, 2026
**Version:** 2.0 (with advanced features)
**Status:** Production Ready ✅
