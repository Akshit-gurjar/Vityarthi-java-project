# StudyPlanner: CLI Academic Study, Task & Schedule Management Platform

- **Student Name**: Akshit
- **Registration Number**: 25BAI10954

---

## 1. Project Overview & Problem Statement

### The Problem
Undergraduate engineering students frequently juggle multiple rigorous academic subjects concurrently, each with distinct assignments, laboratory deliverables, project milestones, and exam revision targets. In typical collegiate environments, students rely on scattered, ad-hoc tracking methods (paper notebooks, chat groups, transient browser tabs). This leads to:
1. **Missed Deadlines**: Lack of dynamic overdue calculation causes urgent deliverables to be overlooked.
2. **Timetable Conflicts**: Study blocks scheduled without temporal validation result in overlapping study plans.
3. **Lack of Workload Insights**: Students have no visibility into which courses have lagging completion rates or heavy credit hour demands.
4. **Setup & Compatibility Frustration**: Many software tools require complex setup, internet logins, or heavy external dependencies that fail during classroom evaluation.

### The Solution: StudyPlanner CLI
StudyPlanner CLI provides a unified, distraction-free command-line environment engineered in pure core Java. It provides students with:
* Course catalog management with credit hour tracking.
* Dynamic task auditing across three priority tiers (`HIGH`, `MEDIUM`, `LOW`) with real-time overdue detection.
* Conflict-checked revision scheduling that warns of overlapping time windows.
* Visual progress analytics using the Java Stream API.
* NIO.2-based CSV exports and timestamped database backups.
* Zero external dependencies: guaranteed to compile and execute instantly on any system with JDK 21+.

---

## 2. Core Modules & Key Features

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                             STUDYPLANNER CLI                                │
│                     Core Functional Capabilities                            │
├──────────────────────┬──────────────────────┬───────────────────────────────┤
│ 1. Subject Catalog   │ 2. Task Tracking     │ 3. Study Timetable            │
│  - Unique Course Code│  - 3-Tier Priority   │  - Start & End Time Intervals │
│  - Credit Hours      │  - ISO Due Dates     │  - Temporal Conflict Engine   │
│  - Cascading Deletes │  - Real-time Overdue │  - Topic Linking              │
├──────────────────────┼──────────────────────┼───────────────────────────────┤
│ 4. Stream Analytics  │ 5. Reports & NIO.2   │ 6. Multithread Concurrency    │
│  - ASCII Progress Bar│  - Text Summary      │  - Background Daemon Scanner  │
│  - Subject Groupings │  - CSV Task Export   │  - Dynamic Header Banner Alert│
│  - Workload Totals   │  - Hot DB Snapshots  │  - Thread-Safe ReadWriteLocks │
└──────────────────────┴──────────────────────┴───────────────────────────────┘
```

### Module Breakdown:
1. **Subject Management (`MOD-01`)**:
   - Create, list, search, update, and delete courses.
   - Enforces unique course codes (e.g. `CS101`, `CS201`) and positive credit values.
   - Foreign-key cascading: deleting a subject cleanly purges all associated tasks and study sessions to preserve relational integrity.
2. **Task Management & Overdue Auditing (`MOD-02`)**:
   - Track tasks with `LOW`, `MEDIUM`, or `HIGH` priority and estimated effort (minutes).
   - Dynamic real-time overdue detection: automatically checks `LocalDate.isBefore(today)` for pending items without requiring manual database recalculations.
   - Filter tasks by status (`PENDING`/`COMPLETED`), priority, due date, or keyword search.
3. **Timetable & Conflict Detection Engine (`MOD-03`)**:
   - Plan study blocks with date, start time (`HH:mm`), end time (`HH:mm`), and topics.
   - Real-time conflict engine: detects overlapping time windows on the same date and alerts the student before scheduling.
4. **Progress & Analytics (`MOD-04`)**:
   - Overall completion rate rendered with dynamic ASCII progress bars (`[#####---------------] 25.0%`).
   - Subject-specific completion rates computed using `Collectors.groupingBy()` and reduction.
   - Total estimated workload hours vs. scheduled study timetable hours.
5. **Reports & NIO.2 File Management (`MOD-05`)**:
   - Formatted academic progress report generation.
   - Export tasks to CSV (`data/exports/tasks_export_<timestamp>.csv`).
   - Export timetable to CSV (`data/exports/schedule_export_<timestamp>.csv`).
   - Snapshot database backups (`data/backups/studyplanner_backup_<timestamp>.dat`).
6. **Multithreaded Background Concurrency (`MOD-06`)**:
   - Background daemon thread powered by `ScheduledExecutorService`.
   - Audits tasks every 60 seconds and updates active alert status in the console header (`>> [ALERT: 1 overdue task(s)!]`).
   - Registers JVM shutdown hooks to cleanly terminate thread pools and save state.
7. **Demo Data Seeder (`MOD-07`)**:
   - Option 6 instantly populates 3 courses, 4 tasks, and 2 study sessions for rapid 2-minute evaluation.
8. **Native Automated Test Suite (`MOD-08`)**:
   - Custom reflection-based test runner (`TestRunner`) executing 24 unit and integration test cases with 100% pass rate.

---

## 3. Quick Start: How to Compile & Run

### Prerequisites
* Standard **Java Development Kit (JDK) 21 or higher** installed (`java -version` and `javac -version`).

---

### Option A: Using One-Click Automation Scripts (Recommended)

#### On Windows (PowerShell or Command Prompt):
```powershell
# 1. Compile all source and test files into bin/
.\compile.bat

# 2. Run the interactive StudyPlanner CLI
.\run.bat

# 3. Run the built-in 24-test automated verification suite
.\test.bat

# 4. (Optional) Build standalone executable JAR
.\package.bat
```
*(Note: Running `.\run.bat` directly will automatically detect if `bin/` needs compiling and compile it for you.)*

#### On Linux / macOS (Terminal):
```bash
chmod +x *.sh
./compile.sh     # Compiles all sources into bin/
./run.sh         # Starts the interactive CLI application
./test.sh        # Executes the 24 automated unit & integration tests
./package.sh     # Builds standalone study-planner-cli.jar
```

---

### Option B: Direct Terminal Commands (Pure Native JDK)

#### 1. Windows PowerShell:
```powershell
# Compile all sources into bin/ (Quotes around sourcepath prevent PowerShell delimiter splitting)
javac -encoding UTF-8 -d bin -sourcepath "src\main\java;src\test\java" src\main\java\com\studyplanner\app\Main.java src\test\java\com\studyplanner\test\TestRunner.java

# Launch the Application:
java -cp bin com.studyplanner.app.Main

# Run the 24-Test Automated Verification Suite:
java -cp bin com.studyplanner.test.TestRunner
```

#### 2. Windows Command Prompt (`cmd.exe`):
```cmd
javac -encoding UTF-8 -d bin -sourcepath "src\main\java;src\test\java" src\main\java\com\studyplanner\app\Main.java src\test\java\com\studyplanner\test\TestRunner.java src\test\java\com\studyplanner\*.java
java -cp bin com.studyplanner.app.Main
java -cp bin com.studyplanner.test.TestRunner
```

#### 3. Linux / macOS:
```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin com.studyplanner.app.Main
java -cp bin com.studyplanner.test.TestRunner
```

#### Standalone Executable JAR (All Platforms):
```bash
# Package:
jar --create --file study-planner-cli.jar --main-class com.studyplanner.app.Main -C bin .

# Run:
java -jar study-planner-cli.jar
```

---

## 4. Step-by-Step 3-Minute Evaluation Walkthrough

Follow these steps to rapidly evaluate all key functional grading criteria in under 3 minutes:

| Step | Action in CLI | What It Demonstrates / What to Look For |
| :---: | :--- | :--- |
| **1** | **Launch Application**<br>`.\run.bat` or `java -cp bin com.studyplanner.app.Main` | Visual ASCII header banner, clean navigation menu, and background daemon initialization. |
| **2** | **Load Demo Data**<br>Type `6` and press Enter | **Demo Data Seeder**: Automatically populates 3 courses (CS101, CS201, CS301), 4 diverse academic tasks, and 2 scheduled study blocks for instant evaluation without manual typing. |
| **3** | **Observe Concurrency Alert**<br>Look at the header banner | **Multithreaded Background Daemon**: The header dynamically displays `>> [ALERT: 1 overdue task(s)!]` powered by `ScheduledExecutorService` scanning task deadlines in real-time. |
| **4** | **Inspect Subjects**<br>Type `1` -> Type `2` | **Subject Management**: View enrolled subjects formatted in an aligned ASCII table with course codes, credit hours, and descriptions. |
| **5** | **Check Tasks & Overdue Auditing**<br>Type `2` -> Type `2` | **Task Management**: Notice dynamic overdue auditing (`LocalDate.isBefore(today)`). Task #1 is highlighted with `[OVERDUE]` tag. Displays three priority tiers (`HIGH`, `MEDIUM`, `LOW`). |
| **6** | **Update Task Status**<br>Type `2` -> Type `7` -> Enter ID `1` | **Lifecycle Transition**: Marks the overdue task as `COMPLETED`. Returning to the main menu clears the overdue alert in the banner. |
| **7** | **Test Timetable & Conflict Engine**<br>Type `3` -> Type `2`<br>Then Type `3` -> Type `1` | **Temporal Conflict Detection**: View existing study sessions. Attempt to schedule a new session on `2026-09-16` between `14:30` and `15:00` (overlapping with session #1). Notice the system warns of an overlap before scheduling. |
| **8** | **View Functional Analytics**<br>Type `4` | **Java Stream API**: Renders visual ASCII progress bars (`[#####---------------] 25.0%`), subject-wise completion percentages using `Collectors.groupingBy`, and cumulative estimated workload vs study hours. |
| **9** | **Export Reports & Backups**<br>Type `5` -> Type `1`, `2`, `3`, `4` | **Java NIO.2 File Operations**: Generates an academic progress report, exports structured CSV files to `data/exports/`, and creates an instantaneous snapshot database backup in `data/backups/`. |
| **10** | **Graceful Termination**<br>Type `7` | **Clean JVM Shutdown Hook**: Gracefully shuts down daemon thread pools, flushes storage buffers, and exits with status 0. |

---

## 5. Java Syllabus & Academic Rubric Mapping

This project was engineered to showcase authentic mastery of the core Java programming curriculum. The table below maps course concepts directly to their concrete implementation in the codebase:

| Syllabus / Rubric Concept | Realization in Codebase | Concrete Code Reference |
| :--- | :--- | :--- |
| **Object-Oriented Programming (OOP)** | Robust domain models with private encapsulated state, public getters/setters, immutable IDs, clean constructors, and overridden `toString()`. | [`Subject.java`](file:///src/main/java/com/studyplanner/model/Subject.java), [`Task.java`](file:///src/main/java/com/studyplanner/model/Task.java), [`StudySession.java`](file:///src/main/java/com/studyplanner/model/StudySession.java) |
| **Custom Exception Handling** | Well-structured hierarchy of unchecked custom business exceptions for validation, lookups, and persistence errors. | [`InvalidInputException.java`](file:///src/main/java/com/studyplanner/exception/InvalidInputException.java), [`SubjectNotFoundException.java`](file:///src/main/java/com/studyplanner/exception/SubjectNotFoundException.java), [`TaskNotFoundException.java`](file:///src/main/java/com/studyplanner/exception/TaskNotFoundException.java), [`DatabaseException.java`](file:///src/main/java/com/studyplanner/exception/DatabaseException.java) |
| **Defensive Input Validation** | Centralized validator parsing and verifying integers, positive credits, ISO-8601 dates (`YYYY-MM-DD`), 24-hr time (`HH:mm`), and non-empty strings. | [`InputValidator.java`](file:///src/main/java/com/studyplanner/util/InputValidator.java) |
| **Collections Framework & Generics** | Heavy utilization of `List<T>`, `Map<K, V>`, `LinkedHashMap`, `Set<T>`, and `Collections.unmodifiableList` for data isolation and relational integrity. | [`SubjectRepository.java`](file:///src/main/java/com/studyplanner/repository/SubjectRepository.java), [`TaskRepository.java`](file:///src/main/java/com/studyplanner/repository/TaskRepository.java) |
| **Java Stream API & Lambdas** | Functional stream pipelines: filtering by priority/status, computing completion rates, and subject-wise grouping via `Collectors.groupingBy()`. | [`AnalyticsService.java`](file:///src/main/java/com/studyplanner/service/AnalyticsService.java) |
| **Multithreading & Concurrency** | Periodic daemon auditing via `ScheduledExecutorService`, transactional thread-safety with `ReentrantReadWriteLock`, and clean JVM shutdown hooks. | [`ReminderService.java`](file:///src/main/java/com/studyplanner/service/ReminderService.java), [`DatabaseManager.java`](file:///src/main/java/com/studyplanner/database/DatabaseManager.java) |
| **Java File I/O & NIO.2** | High-performance atomic persistence, formatted CSV exports, and snapshot backups using `java.nio.file.Files`, `Paths`, and `StandardOpenOption`. | [`DatabaseManager.java`](file:///src/main/java/com/studyplanner/database/DatabaseManager.java), [`CsvExporter.java`](file:///src/main/java/com/studyplanner/util/CsvExporter.java), [`BackupManager.java`](file:///src/main/java/com/studyplanner/util/BackupManager.java) |
| **Modern Java Date/Time API** | Strict temporal auditing and conflict detection using `java.time.LocalDate`, `LocalTime`, `Duration`, and `ChronoUnit`. | [`Task.java`](file:///src/main/java/com/studyplanner/model/Task.java), [`ScheduleService.java`](file:///src/main/java/com/studyplanner/service/ScheduleService.java) |
| **Reflection & Custom Annotations** | Built-in native automated test runner inspecting method annotations (`@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`) without JUnit JARs. | [`TestRunner.java`](file:///src/test/java/com/studyplanner/test/TestRunner.java), [`Assertions.java`](file:///src/test/java/com/studyplanner/test/Assertions.java) |
| **Layered Architecture & Separation of Concerns** | Strict 5-tier architecture separating Presentation (`app`), Business Logic (`service`), Access (`repository`), Storage (`database`), and Models (`model`). | Full project structure |

---

## 6. System Architecture & Layered Design

The system implements a decoupled 5-tier architecture in pure native Java:

```text
┌─────────────────────────────────────────────────────────────┐
│                    Terminal Console (CLI)                   │
│                     com.studyplanner.app                    │
│                          (Main.java)                        │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                        Service Layer                        │
│                   com.studyplanner.service                  │
│   (SubjectService, TaskService, ScheduleService,            │
│    AnalyticsService, ReportService, ReminderService)        │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
┌──────────────▼──────────────┐ ┌──────────────▼──────────────┐
│      Repository Layer       │ │      Utilities & Files      │
│  com.studyplanner.repository│ │     com.studyplanner.util   │
│ (SubjectRepo, TaskRepo,     │ │   (CsvExporter, BackupMgr,  │
│  StudySessionRepo)          │ │    InputValidator, NIO.2)   │
└──────────────┬──────────────┘ └─────────────────────────────┘
               │
┌──────────────▼──────────────┐
│   Native Storage Engine     │
│  com.studyplanner.database  │
│  (DatabaseManager.java)     │
│  data/studyplanner.dat      │
└─────────────────────────────┘
```

### Architectural Benefits:
- **Separation of Concerns**: The CLI presentation layer contains zero storage logic or complex business calculations.
- **Testability**: Service and utility classes can be tested independently of the console UI.
- **Maintainability**: Changing storage formats or UI formatting has zero impact on core business services.

---

## 7. Automated Testing Suite (24 Tests, 100% Pass Rate)

The project includes a built-in reflection-based test framework (`com.studyplanner.test`) with custom annotations (`@Test`, `@BeforeEach`, `@AfterEach`, `@DisplayName`) and assertion helpers (`assertEquals`, `assertTrue`, `assertThrows`).

Execute tests at any time via:
```powershell
.\test.bat
# or: java -cp bin com.studyplanner.test.TestRunner
```

### Test Coverage Table:

| Test Suite Class | File Location | Count | Focus Area Tested |
| :--- | :--- | :---: | :--- |
| **`InputValidatorTest`** | [`InputValidatorTest.java`](file:///src/test/java/com/studyplanner/InputValidatorTest.java) | 9 | Non-empty strings, positive integers, ISO dates (`YYYY-MM-DD`), 24-hr time (`HH:mm`), priority parsing, boundary limits. |
| **`DatabaseCrudTest`** | [`DatabaseCrudTest.java`](file:///src/test/java/com/studyplanner/DatabaseCrudTest.java) | 3 | Storage engine persistence, auto-increment IDs, relational cascading deletes, session queries. |
| **`TaskServiceTest`** | [`TaskServiceTest.java`](file:///src/test/java/com/studyplanner/TaskServiceTest.java) | 5 | Task creation, non-existent subject rejection, status updates, dynamic overdue auditing, priority filtering. |
| **`AnalyticsServiceTest`** | [`AnalyticsServiceTest.java`](file:///src/test/java/com/studyplanner/AnalyticsServiceTest.java) | 4 | Java Stream API completion rate calculation, subject grouping, workload aggregation, study hour totals. |
| **`ScheduleServiceTest`** | [`ScheduleServiceTest.java`](file:///src/test/java/com/studyplanner/ScheduleServiceTest.java) | 3 | Study session scheduling, duration math, time interval constraints, temporal conflict detection. |
| **Total** | | **24** | **100% Pass Rate (0 Failures, 0 Errors)** |

### Actual Test Runner Output:
```text
================================================================================
            STUDY PLANNER CLI - NATIVE JAVA AUTOMATED TEST RUNNER              
               (Zero External Dependencies / Pure Java SE JDK)                  
================================================================================

--------------------------------------------------------------------------------
Suite: InputValidatorTest
--------------------------------------------------------------------------------
  [✓ PASS] requireNonEmpty should return trimmed string on valid input (16 ms)
  [✓ PASS] parseDate should throw on malformed dates               (9 ms)
  [✓ PASS] parseTime should parse valid 24-hr HH:mm format         (1 ms)
  [✓ PASS] requireNonEmpty should throw InvalidInputException on null or blank input (2 ms)
  [✓ PASS] parsePriority should handle case-insensitive valid strings (2 ms)
  [✓ PASS] parseDate should parse valid ISO YYYY-MM-DD format      (1 ms)
  [✓ PASS] validateTimeRange should throw if end time is before or equal to start time (4 ms)
  [✓ PASS] parsePositiveInt should parse numbers greater than 0    (0 ms)
  [✓ PASS] parsePositiveInt should throw exception on zero, negative numbers, or non-digits (1 ms)

--------------------------------------------------------------------------------
Suite: DatabaseCrudTest
--------------------------------------------------------------------------------
  [✓ PASS] Test StudySession CRUD and retrieval                    (36 ms)
  [✓ PASS] Test Task CRUD operations and foreign key relations     (9 ms)
  [✓ PASS] Test Subject CRUD operations                            (5 ms)

--------------------------------------------------------------------------------
Suite: TaskServiceTest
--------------------------------------------------------------------------------
  [✓ PASS] createTask should reject invalid non-existent subject   (7 ms)
  [✓ PASS] High priority filter returns only pending high priority tasks (9 ms)
  [✓ PASS] markTaskComplete should update status to COMPLETED      (5 ms)
  [✓ PASS] createTask should successfully save a new task          (3 ms)
  [✓ PASS] Overdue detection should correctly identify pending past-due tasks (6 ms)

--------------------------------------------------------------------------------
Suite: AnalyticsServiceTest
--------------------------------------------------------------------------------
  [✓ PASS] getSubjectWiseProgress should return 50% for CS202 and 100% for CS203 (16 ms)
  [✓ PASS] getOverallCompletionRate should calculate 75% for 3 completed out of 4 tasks (8 ms)
  [✓ PASS] Task counts should match expected numbers               (7 ms)
  [✓ PASS] Total estimated workload and study hours should be calculated accurately (12 ms)

--------------------------------------------------------------------------------
Suite: ScheduleServiceTest
--------------------------------------------------------------------------------
  [✓ PASS] createSession should persist a valid study session      (3 ms)
  [✓ PASS] createSession should reject invalid time ranges where end <= start (2 ms)
  [✓ PASS] findConflicts should accurately detect overlapping study sessions on the same date (4 ms)

================================================================================
                               TEST RESULTS SUMMARY                             
================================================================================
  Total Test Cases Executed : 24
  Passed                    : 24
  Failed                    : 0
  Pass Rate                 : 100.0%
  Execution Time            : 263 ms
================================================================================
  BUILD & TEST STATUS: [SUCCESS] ALL NATIVE TESTS PASSED!
================================================================================
```

---

## 8. Native Persistence Engine (`data/studyplanner.dat`)

Instead of requiring external database drivers or SQL installations, StudyPlanner CLI features a transactional file persistence engine built in pure Java SE:

* **Thread Safety**: Uses `ReentrantReadWriteLock` to coordinate concurrent access between background scanner threads and user console input.
* **Relational Integrity**: Enforces auto-increment ID generation, unique subject code constraints, and cascading foreign-key deletions.
* **Atomic Persistence**: Uses Java NIO.2 (`Files.write` with `StandardOpenOption.TRUNCATE_EXISTING`) to prevent file corruption.
* **Human-Readable Format**: Data is stored under clean, structured section headers in `data/studyplanner.dat`:

```text
# STUDYPLANNER NATIVE DATABASE - VERSION 1.0
# AUTO-GENERATED NATIVE STORAGE - DO NOT EDIT MANUALLY
[COUNTERS]
nextSubjectId=4
nextTaskId=5
nextSessionId=3
[SUBJECTS]
1|Java Programming|CS101|4|Core Java, OOP, Streams, Concurrency
2|Database Management Systems|CS201|4|Relational modeling, Normalization
3|Operating Systems|CS301|3|Processes, Threads, CPU Scheduling, Memory
[TASKS]
1|1|Complete Java OOP Lab|Implement inheritance classes|HIGH|2026-09-15|PENDING|120
2|1|Practice Concurrency|Build thread pool service|MEDIUM|2026-09-16|COMPLETED|90
3|2|Normalize DBMS Schema|Apply 3NF & BCNF to design|HIGH|2026-09-19|PENDING|150
4|3|Revise CPU Scheduling|Round robin and SJF algorithms|LOW|2026-09-21|PENDING|60
[SESSIONS]
1|1|2026-09-16|14:00|15:30|Concurrency and ExecutorService|SCHEDULED
2|2|2026-09-17|10:00|11:30|ER Modeling & Normalization|SCHEDULED
```

---

## 9. Project Directory Structure

```text
study-planner-cli/
├── README.md               # Comprehensive evaluation & technical documentation
├── statement.md            # Problem statement, academic scope, and target outcomes
├── project_report.md       # Comprehensive academic project report with technical analysis
├── compile.bat             # Native Windows compilation script (javac)
├── run.bat                 # Native Windows launch script (java)
├── test.bat                # Native Windows automated test suite runner
├── package.bat             # Native Windows standalone JAR packager (jar)
├── compile.sh              # Native Linux/macOS compilation script
├── run.sh                  # Native Linux/macOS launch script
├── test.sh                 # Native Linux/macOS automated test suite runner
├── package.sh              # Native Linux/macOS standalone JAR packager
├── study-planner-cli.jar   # Pre-built standalone executable JAR
├── .gitignore              # Git ignore rules for compiled classes & exports
│
├── data/                   # Application data directory (persisted locally)
│   ├── studyplanner.dat    # Native ACID-like database file
│   ├── exports/            # Automated CSV exports (tasks and schedules)
│   └── backups/            # Hot snapshot database backups
│
└── src/
    ├── main/java/com/studyplanner/
    │   ├── app/
    │   │   └── Main.java                       # Interactive Console UI & Menu Loop
    │   ├── database/
    │   │   └── DatabaseManager.java            # Thread-safe Native Persistence Engine
    │   ├── exception/
    │   │   ├── DatabaseException.java          # Persistence layer exceptions
    │   │   ├── InvalidInputException.java      # Input validation exceptions
    │   │   ├── SubjectNotFoundException.java   # Subject lookup exceptions
    │   │   └── TaskNotFoundException.java      # Missing task exceptions
    │   ├── model/
    │   │   ├── Priority.java                   # Enum: LOW, MEDIUM, HIGH
    │   │   ├── SessionStatus.java              # Enum: SCHEDULED, COMPLETED, CANCELLED
    │   │   ├── StudySession.java               # Timetable entity with conflict detection
    │   │   ├── Subject.java                    # Course entity with credit hours
    │   │   ├── Task.java                       # Task entity with dynamic overdue auditing
    │   │   └── TaskStatus.java                 # Enum: PENDING, COMPLETED
    │   ├── repository/
    │   │   ├── StudySessionRepository.java     # Data queries for study sessions
    │   │   ├── SubjectRepository.java          # Data queries for subjects
    │   │   └── TaskRepository.java             # Data queries for tasks
    │   ├── service/
    │   │   ├── AnalyticsService.java           # Stream API metric calculations
    │   │   ├── ReminderService.java            # Multithreaded ExecutorService alerts
    │   │   ├── ReportService.java              # Report generator & NIO.2 coordinator
    │   │   ├── ScheduleService.java            # Timetable management & conflict checks
    │   │   ├── SubjectService.java             # Subject business validation & lookups
    │   │   └── TaskService.java                # Task workflow & query filters
    │   └── util/
    │       ├── BackupManager.java              # NIO.2 database snapshots
    │       ├── CsvExporter.java                # NIO.2 CSV export utility
    │       └── InputValidator.java             # Defensive input validation helpers
    │
    └── test/java/com/studyplanner/
        ├── test/
        │   ├── Assertions.java                 # Native assertion library
        │   ├── TestRunner.java                 # Native reflection-based test runner
        │   ├── Test.java                       # @Test annotation
        │   ├── DisplayName.java                # @DisplayName annotation
        │   ├── BeforeEach.java                 # @BeforeEach annotation
        │   └── AfterEach.java                  # @AfterEach annotation
        ├── AnalyticsServiceTest.java           # Stream analytics tests
        ├── DatabaseCrudTest.java               # Native persistence integration tests
        ├── InputValidatorTest.java             # Input validation unit tests
        ├── ScheduleServiceTest.java            # Schedule & conflict unit tests
        └── TaskServiceTest.java                # Task lifecycle & overdue tests
```

---

## 10. Evaluator Troubleshooting & FAQ

| Question / Issue | Explanation & Fast Resolution |
| :--- | :--- |
| **Can I run this without installing Maven or Gradle?** | **Yes, 100%!** The project was purposefully designed without Maven or Gradle. It requires only standard JDK 21+. Run `.\run.bat` or `.\test.bat`. |
| **In PowerShell, `javac` says `The module 'src' could not be loaded`** | In PowerShell, unquoted semicolons `;` act as statement separators. Wrap the sourcepath in quotes: `-sourcepath "src\main\java;src\test\java"`, or simply execute `.\compile.bat`. |
| **How do I reset all data to a clean state?** | Simply delete `data/studyplanner.dat` (or select Option 6 in the main menu at any time to reseed standard demo data). |
| **How does conflict detection work?** | In [`ScheduleService.java`](file:///src/main/java/com/studyplanner/service/ScheduleService.java), the system compares `(newStart < existingEnd) && (newEnd > existingStart)` for all sessions on the same calendar date. If true, an overlap warning is triggered. |
| **Where are exported CSVs and backups saved?** | Exported CSVs are placed in `data/exports/` and database backups in `data/backups/`. Both directories are created automatically by Java NIO.2. |

---

## 11. Student Declaration & Authorship

- **Student Name**: Akshit
- **Registration Number**: 25BAI10954
- **Branch**: B.Tech Computer Science & Engineering (Artificial Intelligence & Machine Learning)
- **Institution**: Vellore Institute of Technology (VIT) Bhopal University
- **Contact**: `akshit.25bai10954@vitbhopal.ac.in`
