package com.studyplanner;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.Priority;
import com.studyplanner.model.Subject;
import com.studyplanner.model.Task;
import com.studyplanner.repository.StudySessionRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.service.AnalyticsService;
import com.studyplanner.service.ScheduleService;
import com.studyplanner.service.SubjectService;
import com.studyplanner.service.TaskService;
import com.studyplanner.test.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static com.studyplanner.test.Assertions.*;

/**
 * Unit tests for AnalyticsService calculations using Java Stream API.
 */
public class AnalyticsServiceTest {

    private static final String TEST_DB = "bin/test_analytics.dat";
    private DatabaseManager dbManager;
    private TaskService taskService;
    private AnalyticsService analyticsService;

    @BeforeEach
    public void setUp() throws Exception {
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();

        dbManager = new DatabaseManager(TEST_DB);
        SubjectRepository subjectRepo = new SubjectRepository(dbManager);
        TaskRepository taskRepo = new TaskRepository(dbManager);
        StudySessionRepository sessionRepo = new StudySessionRepository(dbManager);

        SubjectService subjectService = new SubjectService(subjectRepo);
        taskService = new TaskService(taskRepo, subjectService);
        ScheduleService scheduleService = new ScheduleService(sessionRepo, subjectService);
        analyticsService = new AnalyticsService(taskService, subjectService, scheduleService);

        // Populate two subjects
        Subject s1 = subjectService.createSubject("Algorithms", "CS202", 4, "Data structures & algorithms");
        Subject s2 = subjectService.createSubject("Web Development", "CS203", 3, "HTML, CSS, JS");

        // Add 4 tasks: 2 for CS202 (1 done, 1 pending), 2 for CS203 (both done)
        Task t1 = taskService.createTask(s1.getId(), "Dijkstra Algorithm", "Implement priority queue", Priority.HIGH, LocalDate.now().plusDays(2), 120);
        Task t2 = taskService.createTask(s1.getId(), "Merge Sort", "Recursive analysis", Priority.MEDIUM, LocalDate.now().plusDays(1), 60);
        Task t3 = taskService.createTask(s2.getId(), "Responsive Navbar", "Flexbox and media queries", Priority.LOW, LocalDate.now().plusDays(3), 60);
        Task t4 = taskService.createTask(s2.getId(), "Form Validation", "JS regex checking", Priority.MEDIUM, LocalDate.now().plusDays(4), 60);

        taskService.markTaskComplete(t1.getId());
        taskService.markTaskComplete(t3.getId());
        taskService.markTaskComplete(t4.getId());

        // Add 1 scheduled study session (90 minutes = 1.5 hours)
        scheduleService.createSession(s1.getId(), LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(15, 30), "Graph Algorithms Review");
    }

    @AfterEach
    public void tearDown() {
        dbManager.close();
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();
    }

    @Test
    @DisplayName("getOverallCompletionRate should calculate 75% for 3 completed out of 4 tasks")
    public void testOverallCompletionRate() {
        double rate = analyticsService.getOverallCompletionRate();
        assertEquals(75.0, rate, 0.01);
    }

    @Test
    @DisplayName("getSubjectWiseProgress should return 50% for CS202 and 100% for CS203")
    public void testSubjectWiseProgress() {
        Map<String, Double> progress = analyticsService.getSubjectWiseProgress();
        assertEquals(2, progress.size());
        assertEquals(50.0, progress.get("CS202"), 0.01);
        assertEquals(100.0, progress.get("CS203"), 0.01);
    }

    @Test
    @DisplayName("Task counts should match expected numbers")
    public void testTaskCounts() {
        assertEquals(4, analyticsService.getTotalTasksCount());
        assertEquals(3, analyticsService.getCompletedTasksCount());
        assertEquals(1, analyticsService.getPendingTasksCount());
    }

    @Test
    @DisplayName("Total estimated workload and study hours should be calculated accurately")
    public void testStudyHoursCalculation() {
        // Total minutes for tasks: 120 + 60 + 60 + 60 = 300 minutes = 5.0 hours
        assertEquals(5.0, analyticsService.getTotalEstimatedTaskHours(), 0.01);

        // Scheduled session: 90 minutes = 1.5 hours
        assertEquals(1.5, analyticsService.getTotalScheduledStudyHours(), 0.01);
    }
}
