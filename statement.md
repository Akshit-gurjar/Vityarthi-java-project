# VELLORE INSTITUTE OF TECHNOLOGY (VIT) BHOPAL UNIVERSITY
**Course:** Programming in Java | **Academic Session:** Academic Year 2025–2026

---

## Course Project Statement

### Project Specification & Student Submission Details

| Parameter | Specification Details |
| :--- | :--- |
| **Project Title** | **StudyPlanner: CLI Academic Study, Task & Schedule Management Platform** |
| **Student Name** | **Akshit** |
| **Registration Number** | **25BAI10954** |
| **Email** | `akshit.25bai10954@vitbhopal.ac.in` |
| **Academic Program** | B.Tech Computer Science & Engineering (Artificial Intelligence & Machine Learning) |
| **Course Name** | Programming in Java |
| **Development Stack** | 100% Pure Native Java SE 21+ (`java.base`, `java.nio`, `java.time`, `java.util.concurrent`, `java.util.stream`) |
| **Dependencies** | **Zero External Dependencies / No Build Tools** (Compiled & Run natively via `javac` and `java`) |
| **Verification Suite** | 24 Automated Test Cases via Native Test Framework (Pass Rate: 100%) |

---

## 1. Problem Statement

Undergraduate engineering and computer science students frequently manage multiple rigorous courses simultaneously, each with distinct assignments, laboratory deliverables, midterm milestones, and comprehensive revision requirements. In typical collegiate environments, students attempt to track their academic obligations using fragmented, ad-hoc mechanisms:

1. **Scattered Information Channels**: Deadlines, syllabus units, and revision goals are dispersed across paper notebooks, messaging groups, LMS portals, and transient mobile notes.
2. **Lack of Dynamic Overdue & Priority Visibility**: Static to-do lists do not compute temporal state. When an assignment deadline elapses, students receive no automated overdue alerting or urgency re-indexing, resulting in missed submissions and academic penalties.
3. **Uncoordinated Revision Scheduling**: Students often schedule study blocks blindly without checking for temporal overlap or estimated workload, creating timetable conflicts and unrealistic daily targets.
4. **Absence of Quantitative Progress Tracking**: Students lack analytical insight into completion rates across individual courses, preventing informed reallocation of study hours toward weaker or credit-heavy subjects.
5. **Absence of Auditable Backups & Portability**: Existing commercial solutions require constant internet connectivity, proprietary subscriptions, or invasive mobile accounts, offering no local offline persistence or standard data export.

**StudyPlanner CLI** resolves this problem by engineering a centralized, offline-first command-line system in **100% core native Java**. It provides students with a single system of record to manage course credits, track task priorities and deadlines, schedule conflict-checked study sessions, analyze progress metrics through functional data streams, and produce auditable CSV exports and snapshot database backups using Java NIO.2 without relying on heavy external drivers, build managers, or cloud dependencies.

---

## 2. Scope of the Project

The scope of StudyPlanner CLI encompasses an end-to-end academic productivity platform operable purely within standard OS terminal environments (Windows, macOS, Linux) with standard Java Development Kit (JDK 21+) tools.

### In-Scope Functional Boundaries:
- **Academic Subject Management**: Full CRUD persistence for subjects, course codes, positive credit values, and syllabus descriptions, enforcing strict code uniqueness.
- **Task Lifecycle & Overdue Auditing**: Task creation with three-tiered priority indexing (`LOW`, `MEDIUM`, `HIGH`), deadline validation, status transitions (`PENDING`, `COMPLETED`), and automated real-time overdue flagging.
- **Timetable & Study Session Scheduling**: Scheduling study sessions with start/end time windows, linked topics, and real-time temporal conflict detection for overlapping time slots.
- **Progress Analytics Engine**: Calculating overall completion rates, subject-wise progress breakdowns, workload distributions, and planned study hours utilizing Java Stream API.
- **Native Data Persistence & NIO.2 Management**: Zero-dependency transactional file storage engine (`DatabaseManager`) providing thread-safe ACID-like operations, auto-increment sequences, foreign-key cascading deletes, structured text records, automated CSV exports, and NIO.2 timestamped snapshot backups.
- **Multithreaded Background Concurrency**: An autonomous background daemon (`ScheduledExecutorService`) executing periodic scans for overdue deliverables and displaying non-intrusive alert banners on the terminal console.
- **Automated Verification**: Comprehensive native test suite (24 unit and integration tests) using a built-in reflection-based test runner with 100% pass rate.
- **Direct Terminal Portability**: Standalone execution with native `javac`, `java`, and JDK `jar` commands and portable batch/shell scripts (`compile.bat`, `run.bat`, `test.bat`, `package.bat`).

### Out-of-Scope Boundaries:
- External build managers (Maven/Gradle) or heavy third-party JARs (deliberately avoided to showcase authentic mastery of native Core Java SE, multithreading, NIO.2, and object-oriented systems design).
- External web applications or cloud APIs.
- Multi-tenant cloud servers or distributed network synchronization.

---

## 3. Target Users

1. **University & College Engineering Students**: Managing intensive multi-course workloads who require a lightweight, distraction-free terminal tool to track deliverables and revision schedules.
2. **Self-Directed Learners & Programmers**: Individuals preparing for technical certifications or competitive examinations needing task tracking, estimated study hours, and schedule integrity.
3. **Academic Course Instructors & Evaluators**: Reviewing practical applications of object-oriented design, Collections, Generics, Stream API, Concurrency, File I/O, and NIO.2 in a pure native Java student submission.

---

## 4. High-Level Features

| Module ID | Module Name | High-Level Feature Description |
| :--- | :--- | :--- |
| **MOD-01** | **Subject Management** | Create, view, search, update, and delete academic courses with validation on unique course codes and positive credit values. |
| **MOD-02** | **Task Management** | Full task lifecycle management with priority assignment, deadline validation, dynamic overdue detection, and completion toggles. |
| **MOD-03** | **Study Timetable** | Time-slotted study session scheduling with built-in temporal validation and conflict detection for overlapping intervals. |
| **MOD-04** | **Progress & Analytics** | Real-time academic metrics: visual progress bars, subject-specific completion rates, task distributions, and calculated study hours using Java Streams. |
| **MOD-05** | **Reports & NIO.2 Backups** | Text summary generation, automated CSV export for tasks and timetables, and hot database snapshot backups via Java NIO.2. |
| **MOD-06** | **Concurrency Alert Daemon** | `ScheduledExecutorService` running periodically in the background to surface overdue and due-today notifications cleanly in the CLI header. |
| **MOD-07** | **Demo Data Seeder** | Instant sample data provisioning for fast 2-minute evaluation demos without manual typing. |
| **MOD-08** | **Native Verification Suite** | Zero-dependency test framework and runner executing 24 unit tests across all domain layers with 100% pass rate. |

---

## 5. Expected Technical Outcomes & Standards

- **Defensive Robustness**: The application recovers gracefully from invalid formats, malformed dates, negative numbers, and empty strings with zero unhandled runtime crashes.
- **Layered Architecture**: Decoupled multi-tier design separating Presentation (`app`), Business Logic (`service`), Data Access (`repository`), Native Storage (`database`), and Domain Models (`model`).
- **Zero-Configuration Portability**: Runs seamlessly on any standard JDK 21+ installation across Windows, macOS, and Linux without downloading external dependencies or configuring build plugins.
