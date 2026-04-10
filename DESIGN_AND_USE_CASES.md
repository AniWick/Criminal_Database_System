# Criminal Management System Design Notes

## 1. Architecture (MVC)

The project follows MVC with a layered flow:

View -> Facade -> Controller -> Service -> Repository/Database

Packages:
- View: [view/MainUI.java](view/MainUI.java)
- Controller: [controller/](controller)
- Service: [service/](service)
- Facade: [facade/](facade)
- Model/Data: [model/](model), [database/CriminalDatabase.java](database/CriminalDatabase.java)

The view only handles input/output, business rules are in services, and persistence is centralized in the database class.

## 2. Major Features (Use Cases)

### Use Case 1: User Login / Authentication

Actor: Officer or detective.

Flow:
1. User enters username and password in [view/MainUI.java](view/MainUI.java).
2. Request goes through [facade/CriminalManagementFacade.java](facade/CriminalManagementFacade.java).
3. [controller/LoginController.java](controller/LoginController.java) forwards to [service/AuthService.java](service/AuthService.java).
4. [database/CriminalDatabase.java](database/CriminalDatabase.java) validates credentials from stored users.

### Use Case 2: Register Criminal (Add Criminal Record)

Actor: Logged-in officer.

Flow:
1. UI collects criminal details (ID, name, age, crime).
2. [controller/CriminalController.java](controller/CriminalController.java) validates input with [validation/CriminalValidator.java](validation/CriminalValidator.java).
3. Controller builds `Criminal` using Builder pattern.
4. [service/CriminalService.java](service/CriminalService.java) saves via [repository/InMemoryCriminalRepository.java](repository/InMemoryCriminalRepository.java).

### Use Case 3: Update Criminal Record

Actor: Logged-in officer.

Flow:
1. User supplies criminal ID and updated crime type.
2. Controller -> Service -> Repository updates persistence.
3. [database/CriminalDatabase.java](database/CriminalDatabase.java) saves to disk.

### Use Case 4: Search Criminal

Actor: Logged-in officer.

Flow:
1. User searches by criminal ID.
2. [service/CriminalService.java](service/CriminalService.java) uses Strategy pattern ([strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java), [strategy/CriminalIdSearchStrategy.java](strategy/CriminalIdSearchStrategy.java)).
3. Repository returns matching record from the database store.

### Use Case 5: Manage Cases (Create and Assign Cases)

Actor: Logged-in officer.

Flow:
1. User creates a case with case ID, criminal ID, description, and optional assigned officer.
2. [controller/CaseController.java](controller/CaseController.java) creates model object through [model/ModelFactory.java](model/ModelFactory.java).
3. [service/CaseService.java](service/CaseService.java) stores the case.
4. User can assign/reassign case ownership later using case ID + officer name.

## 3. Minor Features

### Use Case 6: Add Evidence

Actor: Logged-in officer.

Flow:
1. User enters evidence ID, case ID, and evidence type.
2. [controller/EvidenceController.java](controller/EvidenceController.java) creates evidence via factory.
3. [service/EvidenceService.java](service/EvidenceService.java) writes to [database/CriminalDatabase.java](database/CriminalDatabase.java).

### Use Case 7: Store Biometric Data

Actor: Logged-in officer.

Flow:
1. User enters criminal ID, fingerprint, and DNA.
2. [controller/BiometricController.java](controller/BiometricController.java) builds biometric object.
3. [service/BiometricService.java](service/BiometricService.java) stores/retrieves biometric data in database.

### Use Case 8: View Criminal Details

Actor: Logged-in officer.

Flow:
1. User requests details by criminal ID.
2. Facade delegates to criminal controller search/details method.
3. UI displays ID, name, age, and crime.

### Use Case 9: List All Cases

Actor: Logged-in officer.

Flow:
1. User chooses list all cases from menu.
2. [controller/CaseController.java](controller/CaseController.java) retrieves all case records via service.
3. UI prints case ID, criminal ID, assigned officer, and description.

## 4. Design Patterns Implemented

### Singleton
- File: [app/AppContext.java](app/AppContext.java)
- Why: one global access point for application facade.

### Facade
- Files: [facade/CriminalManagement.java](facade/CriminalManagement.java), [facade/CriminalManagementFacade.java](facade/CriminalManagementFacade.java)
- Why: UI talks to one API instead of many internal classes.

### Builder
- File: [model/Criminal.java](model/Criminal.java)
- Why: clean criminal object construction.

### Factory
- File: [model/ModelFactory.java](model/ModelFactory.java)
- Why: centralized model creation for criminal/case/evidence/biometric/user.

### Strategy
- Files: [strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java), [strategy/CriminalIdSearchStrategy.java](strategy/CriminalIdSearchStrategy.java)
- Why: search behavior remains extensible.

## 5. Design Principles Visible in Code

### Single Responsibility Principle (SRP)
- Validators validate.
- Controllers coordinate flows.
- Services keep business rules.
- Database handles persistence.

### Open/Closed Principle (OCP)
- Search strategy can be extended by adding new strategy classes.
- Factory can be extended with new model creation methods.

### Dependency Inversion Principle (DIP)
- UI depends on facade abstraction.
- Service depends on repository/strategy abstractions.

### Interface Segregation Principle (ISP)
- Focused interfaces like `CriminalManagement`, `CriminalRepository`, and search strategy.

## 6. Current Menu Coverage in UI

[view/MainUI.java](view/MainUI.java) includes flows for:
- Login/authentication
- Register/add criminal
- Search criminal
- Update/delete criminal
- View criminal details
- List all criminals
- Create case
- Assign case
- List all cases
- Add evidence
- List evidence by case
- Store biometric
- View biometric

## 7. Quick Demo Script

1. Log in using `admin / admin123`.
2. Register a new criminal.
3. Search and view details of that criminal.
4. Create a case and assign it to an officer.
5. Add evidence to the case.
6. Store biometric data for the criminal.
7. List all cases.
