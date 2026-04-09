# Criminal Management System Design Notes

## 1. Project Architecture

The project follows MVC:

- View: [view/MainUI.java](view/MainUI.java)
- Controller: [controller/CriminalController.java](controller/CriminalController.java), [controller/CaseController.java](controller/CaseController.java), [controller/LoginController.java](controller/LoginController.java)
- Service: [service/CriminalService.java](service/CriminalService.java), [service/CaseService.java](service/CaseService.java), [service/AuthService.java](service/AuthService.java), [service/BiometricService.java](service/BiometricService.java)
- Model/Data: [model/](model), [database/CriminalDatabase.java](database/CriminalDatabase.java)

The UI reads input, controllers coordinate requests, services hold business rules, and the database class persists the data snapshot.

## 2. Use Cases

### Use Case 1: Login

Actor: Officer or detective.

Flow:
1. User enters username and password in the console UI.
2. [view/MainUI.java](view/MainUI.java) sends the credentials to [facade/CriminalManagementFacade.java](facade/CriminalManagementFacade.java).
3. The facade forwards the request to [controller/LoginController.java](controller/LoginController.java).
4. [service/AuthService.java](service/AuthService.java) checks the user in [database/CriminalDatabase.java](database/CriminalDatabase.java).

### Use Case 2: Add Criminal

Actor: Logged-in officer.

Flow:
1. The UI collects criminal details.
2. [controller/CriminalController.java](controller/CriminalController.java) validates the input using [validation/CriminalValidator.java](validation/CriminalValidator.java).
3. The controller uses the Builder in [model/Criminal.java](model/Criminal.java) to create the object.
4. [service/CriminalService.java](service/CriminalService.java) saves it through [repository/InMemoryCriminalRepository.java](repository/InMemoryCriminalRepository.java).

### Use Case 3: Search Criminal

Actor: Logged-in officer.

Flow:
1. The UI requests a criminal by ID.
2. The controller delegates to [service/CriminalService.java](service/CriminalService.java).
3. The service uses the Strategy pattern through [strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java) and [strategy/CriminalIdSearchStrategy.java](strategy/CriminalIdSearchStrategy.java).
4. The repository retrieves the record from the database store.

### Use Case 4: Update or Delete Criminal

Actor: Logged-in officer.

Flow:
1. The UI sends the criminal ID and new data.
2. The controller forwards the operation to the service layer.
3. The service calls the repository abstraction.
4. [database/CriminalDatabase.java](database/CriminalDatabase.java) updates or removes the record and saves the snapshot.

### Use Case 5: Create Case Record

Actor: Logged-in officer.

Flow:
1. The UI or controller supplies case details.
2. [controller/CaseController.java](controller/CaseController.java) creates the object through [model/ModelFactory.java](model/ModelFactory.java).
3. [service/CaseService.java](service/CaseService.java) stores the case in [database/CriminalDatabase.java](database/CriminalDatabase.java).

## 3. Design Patterns Implemented

### Singleton

Implemented in [app/AppContext.java](app/AppContext.java).

How it was done:
- A single private static instance is created.
- The constructor is private.
- `getInstance()` returns the same global object.

Why it matters:
- The application has one central place to access the main facade.

### Facade

Implemented in [facade/CriminalManagementFacade.java](facade/CriminalManagementFacade.java).

How it was done:
- The facade exposes a simple interface for login and criminal operations.
- It hides the controller details from the UI.

Why it matters:
- The UI does not need to know the internal service/controller structure.

### Builder

Implemented in [model/Criminal.java](model/Criminal.java).

How it was done:
- The nested `Builder` class collects field values step by step.
- The controller builds a `Criminal` using chained methods.

Why it matters:
- It makes object creation clearer and easier to extend later.

### Factory

Implemented in [model/ModelFactory.java](model/ModelFactory.java).

How it was done:
- Static factory methods create `Criminal`, `CaseRecord`, `Evidence`, `BiometricData`, and `User` objects.
- Seed data in [database/CriminalDatabase.java](database/CriminalDatabase.java) uses the factory methods.

Why it matters:
- Object creation is centralized in one place.

### Strategy

Implemented in [strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java) and [strategy/CriminalIdSearchStrategy.java](strategy/CriminalIdSearchStrategy.java).

How it was done:
- The service depends on the strategy interface instead of a concrete search method.
- Search behavior can be replaced without changing the controller or UI.

Why it matters:
- Search logic can grow later, for example search by name or crime type.

## 4. Design Principles Implemented

### Single Responsibility Principle

Visible in:
- [validation/CriminalValidator.java](validation/CriminalValidator.java) handles validation only.
- [repository/InMemoryCriminalRepository.java](repository/InMemoryCriminalRepository.java) handles persistence delegation only.
- Controllers focus on request coordination, not data storage.

How it was done:
- Validation was separated from object creation and persistence.
- Data access was moved behind a repository abstraction.

### Open/Closed Principle

Visible in:
- [strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java)
- [model/ModelFactory.java](model/ModelFactory.java)

How it was done:
- New search methods can be added as new strategy classes.
- New object creation rules can be added in the factory without changing callers.

### Dependency Inversion Principle

Visible in:
- [service/CriminalService.java](service/CriminalService.java)
- [repository/CriminalRepository.java](repository/CriminalRepository.java)
- [facade/CriminalManagement.java](facade/CriminalManagement.java)

How it was done:
- High-level code depends on interfaces instead of concrete classes.
- The UI depends on the facade interface.
- The service depends on repository and strategy interfaces.

### Interface Segregation Principle

Visible in:
- [facade/CriminalManagement.java](facade/CriminalManagement.java)
- [repository/CriminalRepository.java](repository/CriminalRepository.java)
- [strategy/CriminalSearchStrategy.java](strategy/CriminalSearchStrategy.java)

How it was done:
- Each interface is small and focused on one role.
- Classes only implement methods they actually need.

## 5. Presentation Points

- MVC is shown by the split between `view`, `controller`, `service`, and `database` packages.
- Singleton is shown by the single application context instance.
- Facade is shown by the one-stop `CriminalManagementFacade` used by the UI.
- Builder is shown by the nested builder inside `Criminal`.
- Factory is shown by the static methods in `ModelFactory`.
- Strategy is shown by the search abstraction in the `strategy` package.
- SRP, OCP, DIP, and ISP are visible in the separate validator, repository, service, and interface layers.

## 6. Short Demo Script

1. Log in using `admin / admin123`.
2. Add a criminal and explain the Builder and validation.
3. Search a criminal and explain the Strategy + Repository flow.
4. Create a case and explain the Factory.
5. Show `AppContext` and explain Singleton + Facade.