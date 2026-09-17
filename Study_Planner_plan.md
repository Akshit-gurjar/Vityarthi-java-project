# Study Planner CLI — Implementation Plan

## 1. Project Overview

**Project:** Study Planner CLI  
**Type:** Java Command-Line Application  
**Goal:** Build a simple but complete student study-management system for subjects, tasks, deadlines, study sessions, progress, reports and backups.

The project must be fully executable from the command line. Keep it as a core Java project; do not add a web GUI or unnecessary frameworks.

---

## 2. Mandatory Requirements

The implementation must satisfy the course-project requirements:

- Java CLI application
- At least 3 major functional modules
- 5–10+ meaningful classes/files
- Proper package structure
- Clear input/output and logical workflow
- OOP
- Collections and Generics
- Stream API and Lambdas
- Exception handling and custom exceptions
- Java Date/Time API
- Java NIO.2 file handling
- Native Core Java Persistence Engine (Thread-Safe File Database)
- Native CRUD operations & Cascading Relations
- Multithreading using `ExecutorService` for a meaningful reminder/checker task
- Native automated unit tests via built-in Test Runner
- `README.md` at repository root
- `statement.md`
- Architecture/workflow/UML/ER documentation
- Real screenshots of the final working CLI in README
- Git-ready project structure
- No fake/placeholder implementation

The application must not crash on normal invalid input.

---

# 3. Major Functional Modules

## Module 1 — Subject Management

Functions:

1. Add Subject
2. View Subjects
3. Search Subject
4. Update Subject
5. Delete Subject

Subject fields:

- ID
- Name
- Code
- Credits
- Description

Validation:

- Name cannot be empty
- Code cannot be empty
- Credits must be positive
- Duplicate subject code must be rejected

---

## Module 2 — Task Management

Functions:

1. Add Task
2. View All Tasks
3. Search Tasks
4. Filter Tasks
5. Update Task
6. Delete Task
7. Mark Task Complete
8. Mark Task Pending

Task fields:

- ID
- Subject ID
- Title
- Description
- Priority
- Deadline
- Status
- Estimated study time

Priority:

```text
LOW
MEDIUM
HIGH
```

Status:

```text
PENDING
COMPLETED
```

The system must automatically identify overdue pending tasks.

---

## Module 3 — Study Schedule

Functions:

1. Add Study Session
2. View Schedule
3. Update Study Session
4. Delete Study Session
5. Detect schedule conflicts

Study session fields:

- ID
- Subject
- Date
- Start time
- End time
- Topic
- Status

Rules:

- End time must be after start time
- Required fields cannot be empty
- Conflicting sessions should produce a clear warning

---

## Module 4 — Progress & Analytics

Show:

- Overall completion percentage
- Subject-wise progress
- Completed task count
- Pending task count
- Overdue task count
- Total planned study hours
- High-priority pending tasks

Use Stream API for:

- Filtering
- Sorting
- Grouping
- Counting
- Progress calculation

Example:

```text
========== PROGRESS ==========

Overall Progress : 72%

Java              80%
DBMS              65%
Operating Systems 70%

Completed Tasks : 36
Pending Tasks   : 14
Overdue Tasks   : 3
Study Hours     : 42.5
```

---

## Module 5 — Reports & Backup

Functions:

1. Generate progress report
2. Export tasks to CSV
3. Export schedule to CSV
4. Backup application data
5. Show backup/export location

Use Java NIO.2 (`Path`, `Files`, etc.).

Directories:

```text
data/
├── exports/
└── backups/
```

Create directories automatically if missing.

---

# 4. Main CLI

The application should start with:

```text
========================================
           STUDY PLANNER CLI
========================================

1. Subject Management
2. Task Management
3. Study Schedule
4. Progress & Analytics
5. Reports & Backup
6. Exit

Enter choice:
```

Task submenu example:

```text
----------- TASK MANAGEMENT -----------

1. Add Task
2. View All Tasks
3. Search Task
4. Filter Tasks
5. Update Task
6. Delete Task
7. Mark Task Complete
8. Back

Enter choice:
```

Keep the CLI clean and easy to demonstrate.

---

# 5. Recommended Project Structure

Use Maven.

```text
study-planner-cli/
├── pom.xml
├── README.md
├── statement.md
├── plan.md
│
├── docs/
│   ├── architecture.md
│   ├── workflow.md
│   └── diagrams/
│
├── src/
│   ├── main/java/com/studyplanner/
│   │   ├── app/
│   │   │   └── Main.java
│   │   ├── model/
│   │   │   ├── Subject.java
│   │   │   ├── Task.java
│   │   │   ├── StudySession.java
│   │   │   ├── Priority.java
│   │   │   └── TaskStatus.java
│   │   ├── service/
│   │   │   ├── SubjectService.java
│   │   │   ├── TaskService.java
│   │   │   ├── ScheduleService.java
│   │   │   ├── AnalyticsService.java
│   │   │   └── ReportService.java
│   │   ├── repository/
│   │   │   ├── SubjectRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   └── StudySessionRepository.java
│   │   ├── database/
│   │   │   └── DatabaseManager.java
│   │   ├── exception/
│   │   │   ├── InvalidInputException.java
│   │   │   ├── SubjectNotFoundException.java
│   │   │   └── TaskNotFoundException.java
│   │   └── util/
│   │       ├── InputValidator.java
│   │       ├── CsvExporter.java
│   │       └── BackupManager.java
│   │
│   └── test/java/com/studyplanner/
│       ├── TaskServiceTest.java
│       ├── AnalyticsServiceTest.java
│       └── InputValidatorTest.java
│
└── data/
    ├── exports/
    └── backups/
```

Do not create meaningless classes just to increase the class count.

---

# 6. Java Syllabus Mapping

## Fundamentals

Demonstrate:

- Variables and data types
- Operators
- Conditions
- Loops
- Switch
- Strings
- Arrays where useful
- Console input/output

## OOP

Clearly demonstrate:

- Encapsulation
- Constructors
- Inheritance where logically appropriate
- Polymorphism
- Abstraction
- Interfaces
- Static members
- Method overriding

Do not force inheritance/design patterns where they do not make sense.

## Collections

Use meaningfully:

```java
List<Task>
Map<Integer, Subject>
Set<String>
```

Include ArrayList, HashMap and HashSet where appropriate.

## Generics

Use generic repository/helper methods where useful.

## Streams + Lambdas

Use them for filtering, sorting, grouping, counting and analytics.

## Exception Handling

Create:

```text
InvalidInputException
SubjectNotFoundException
TaskNotFoundException
```

Handle invalid input, invalid dates/times, missing records, duplicate subjects, database errors and file errors.

## Date/Time API

Use:

```java
LocalDate
LocalTime
LocalDateTime
```

for deadlines and study sessions.

## NIO.2

Use:

```java
Path
Files
```

for CSV export and backups.

## JDBC

Use:

```text
Connection
PreparedStatement
ResultSet
SQLException
```

Never concatenate user input directly into SQL.

## Concurrency

Use `ExecutorService` for a small background reminder/checker.

---

# 7. SQLite Database

Database:

```text
data/studyplanner.db
```

Tables:

### subjects

```text
id
name
code
credits
description
```

### tasks

```text
id
subject_id
title
description
priority
deadline
status
estimated_minutes
```

### study_sessions

```text
id
subject_id
session_date
start_time
end_time
topic
status
```

Use foreign keys for `subject_id`.

`DatabaseManager` should:

- Open connection
- Initialize database
- Create tables if they do not exist

Repositories should implement CRUD.

---

# 8. Concurrency

Create a small `ReminderService`.

It should use `ExecutorService` to periodically check for:

- Overdue tasks
- Tasks due soon

Example:

```text
[REMINDER]
You have 2 pending tasks due today.
```

The executor must be shut down cleanly when the application exits.

Do not build complicated multithreading.

---

# 9. Validation

Use a reusable `InputValidator`.

Examples:

```text
Subject name:
Cannot be empty.

Credits:
Must be greater than 0.

Priority:
LOW / MEDIUM / HIGH

Deadline:
YYYY-MM-DD

Time:
HH:MM
```

The application should recover from invalid input and return to the appropriate menu.

---

# 10. Testing

Use JUnit 5.

Test at least:

### Task

- Add task
- Complete task
- Invalid task
- Overdue detection

### Analytics

- Completion percentage
- Pending count
- Completed count
- Subject-wise progress

### Validation

- Empty input
- Invalid number
- Invalid date
- Invalid priority

### Database

Test basic CRUD operations.

All tests must actually pass before submission.

---

# 11. README.md

Create at repository root.

README must include:

1. Project title
2. Overview
3. Features
4. Technologies
5. Requirements
6. Installation
7. Configuration if required
8. Exact CLI run command
9. Testing command
10. Project structure
11. Database information
12. Screenshots

Example setup:

```bash
git clone <REPOSITORY-URL>
cd study-planner-cli
mvn clean install
mvn exec:java
```

Testing:

```bash
mvn test
```

Use the actual commands that work in the final project.

### Screenshots

At the END of README add real screenshots captured from the final working application.

Recommended:

- Main menu
- Subject management
- Task management
- Progress analytics
- Export/report
- Invalid-input handling

Never use fake screenshots or placeholder images.

---

# 12. statement.md

Create:

```text
# Problem Statement

Students often manage academic tasks, deadlines and study sessions
using scattered notes or multiple applications. This makes it difficult
to track pending work, monitor progress and maintain an organized study
schedule.

Study Planner CLI provides a centralized command-line solution for
managing subjects, academic tasks, study sessions and progress.
```

Also include:

- Scope
- Target Users
- Objectives
- High-level Features
- Expected Outcome

Keep it natural and consistent with the actual implementation.

---

# 13. Documentation

Create:

```text
docs/
├── architecture.md
├── workflow.md
└── diagrams/
```

Architecture:

```text
CLI
 ↓
Services
 ↓
Repositories
 ↓
SQLite Database
```

Also document:

```text
CLI → Service → Repository → Database
```

Required diagrams:

1. System Architecture Diagram
2. Process Flow / Workflow Diagram
3. Use Case Diagram
4. Class Diagram
5. Sequence Diagram
6. ER Diagram

Every diagram must match the actual implementation.

---

# 14. Non-Functional Requirements

Include at least:

### Usability
Clear menus and understandable messages.

### Reliability
Invalid input should not crash the application.

### Performance
Normal student-sized data should be processed quickly.

### Maintainability
Use separate model, service, repository and utility layers.

### Portability
Run on systems supporting the documented Java/Maven versions.

### Security
Validate input and use prepared SQL statements.

---

# 15. Git Workflow

Use meaningful commits such as:

```text
Initial Maven project setup
Add subject management
Add task management
Add study schedule
Add analytics
Add JDBC database
Add CSV export and backup
Add reminder service
Add unit tests
Add documentation
Add README screenshots
Final cleanup
```

---

# 16. Maven Dependencies

Keep dependencies minimal.

Required:

- SQLite JDBC driver
- JUnit 5
- Exec Maven Plugin if needed

Do not use:

- Spring Boot
- Hibernate
- Web frameworks
- External APIs
- Large unnecessary libraries

This is intended to be a core Java course project.

---

# 17. Demo Data

Provide a simple demo/seed option or clearly documented sample workflow.

Example subjects:

```text
Java Programming
Database Management Systems
Operating Systems
```

Example tasks:

```text
Complete Java OOP assignment
Practice JDBC
Prepare DBMS normalization
Revise process scheduling
```

Do not overwrite existing data without warning.

---

# 18. Evaluator Demo Flow

The final project should be demonstrable in approximately 5–10 minutes:

```text
1. Start application
2. Add subject
3. Add 2–3 tasks
4. View tasks
5. Mark one task complete
6. Add study session
7. View analytics
8. Show pending/overdue task
9. Export CSV
10. Create backup
11. Run tests
```

This should demonstrate the major modules and Java concepts.

---

# 19. Implementation Order

## Phase 1 — Setup

- Maven project
- Package structure
- Main class
- CLI menu
- Git

## Phase 2 — Models

- Subject
- Task
- StudySession
- Enums

## Phase 3 — Database

- SQLite connection
- DatabaseManager
- Table creation

## Phase 4 — Repositories

- SubjectRepository
- TaskRepository
- StudySessionRepository
- CRUD

## Phase 5 — Services

- SubjectService
- TaskService
- ScheduleService

## Phase 6 — Validation

- InputValidator
- Custom exceptions
- Error handling

## Phase 7 — Analytics

- Progress
- Filtering
- Sorting
- Grouping
- Stream API

## Phase 8 — File Handling

- CSV export
- Backup
- NIO.2

## Phase 9 — Concurrency

- ReminderService
- ExecutorService
- Graceful shutdown

## Phase 10 — Testing

- Unit tests
- Validation tests
- Database tests

## Phase 11 — Documentation

- statement.md
- architecture.md
- workflow.md
- diagrams
- README.md

## Phase 12 — Final Verification

Run:

```bash
mvn clean test
```

Then run the application manually from the command line.

Fix all errors before finalizing.

---

# 20. Final Checklist

### Functionality

- [ ] Subject CRUD
- [ ] Task CRUD
- [ ] Complete/pending task
- [ ] Deadline handling
- [ ] Overdue detection
- [ ] Study schedule
- [ ] Conflict detection
- [ ] Analytics
- [ ] CSV export
- [ ] Backup
- [ ] Reminder service
- [ ] Clean application exit

### Java Concepts

- [ ] OOP
- [ ] Encapsulation
- [ ] Abstraction
- [ ] Interfaces
- [ ] Polymorphism
- [ ] Inheritance where appropriate
- [ ] Collections
- [ ] Generics
- [ ] Streams
- [ ] Lambdas
- [ ] Exception handling
- [ ] Custom exceptions
- [ ] Date/Time API
- [ ] NIO.2
- [ ] Multithreading
- [ ] ExecutorService
- [ ] JDBC
- [ ] SQL CRUD

### Quality

- [ ] 5–10+ meaningful classes/files
- [ ] Proper package structure
- [ ] No absolute local paths
- [ ] No unused dependencies
- [ ] Clear names
- [ ] Useful comments
- [ ] No fake features
- [ ] No fake test results
- [ ] No fake screenshots

### Submission

- [ ] Public GitHub repository
- [ ] Correct repository root URL
- [ ] README at root
- [ ] CLI setup instructions tested
- [ ] CLI run command tested
- [ ] `mvn test` passes
- [ ] statement.md exists
- [ ] Architecture documented
- [ ] Workflow documented
- [ ] UML diagrams prepared
- [ ] ER diagram prepared
- [ ] Real screenshots added at end of README

---

# 21. Final Instruction

Build the project completely. Do not leave placeholder methods or menu options.

After implementation:

1. Build the project.
2. Run all tests.
3. Run the CLI manually.
4. Test the complete evaluator workflow.
5. Fix all errors.
6. Capture screenshots from the final working application.
7. Add the screenshots to the end of README.
8. Clone the repository into a clean directory and verify that the README commands work.
9. Remove unnecessary generated/IDE files.
10. Ensure documentation matches the actual code.

A fresh clone must be able to build and run using only the commands documented in README.
