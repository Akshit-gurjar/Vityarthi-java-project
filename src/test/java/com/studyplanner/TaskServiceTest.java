package com.studyplanner;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.exception.SubjectNotFoundException;
import com.studyplanner.model.Priority;
import com.studyplanner.model.Subject;
import com.studyplanner.model.Task;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.service.SubjectService;
import com.studyplanner.service.TaskService;
import com.studyplanner.test.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static com.studyplanner.test.Assertions.*;

/**
 * Unit tests for TaskService business logic and workflow.
 */
public class TaskServiceTest {

    private static final String TEST_DB = "bin/test_taskservice.dat";
    private DatabaseManager dbManager;
    private TaskService taskService;
    private Subject testSubject;

    @BeforeEach
    public void setUp() throws InvalidInputException {
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();

        dbManager = new DatabaseManager(TEST_DB);
        SubjectRepository subjectRepo = new SubjectRepository(dbManager);
        TaskRepository taskRepo = new TaskRepository(dbManager);
        SubjectService subjectService = new SubjectService(subjectRepo);

        taskService = new TaskService(taskRepo, subjectService);
        testSubject = subjectService.createSubject("Computer Networks", "CS401", 3, "OSI Model, TCP/IP");
    }

    @AfterEach
    public void tearDown() {
        dbManager.close();
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();
    }

    @Test
    @DisplayName("createTask should successfully save a new task")
    public void testCreateTaskSuccess() throws Exception {
        Task task = taskService.createTask(
                testSubject.getId(),
                "Study Subnetting",
                "CIDR notation exercises",
                Priority.HIGH,
                LocalDate.now().plusDays(3),
                60
        );

        assertNotNull(task);
        assertTrue(task.getId() > 0);
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals("Study Subnetting", task.getTitle());
    }

    @Test
    @DisplayName("createTask should reject invalid non-existent subject")
    public void testCreateTaskInvalidSubject() {
        assertThrows(SubjectNotFoundException.class, () -> {
            taskService.createTask(9999, "Ghost Task", "None", Priority.LOW, LocalDate.now(), 30);
        });
    }

    @Test
    @DisplayName("markTaskComplete should update status to COMPLETED")
    public void testMarkTaskComplete() throws Exception {
        Task task = taskService.createTask(testSubject.getId(), "Lab Report", "Packet Tracer", Priority.MEDIUM, LocalDate.now().plusDays(1), 45);
        assertEquals(TaskStatus.PENDING, task.getStatus());

        taskService.markTaskComplete(task.getId());
        Task updated = taskService.getTaskById(task.getId());
        assertEquals(TaskStatus.COMPLETED, updated.getStatus());
    }

    @Test
    @DisplayName("Overdue detection should correctly identify pending past-due tasks")
    public void testOverdueDetection() throws Exception {
        // Overdue pending task
        taskService.createTask(testSubject.getId(), "Overdue Lab", "Late assignment", Priority.HIGH, LocalDate.now().minusDays(2), 60);

        // Future pending task
        taskService.createTask(testSubject.getId(), "Future Quiz", "Next week", Priority.LOW, LocalDate.now().plusDays(4), 30);

        // Overdue but COMPLETED task (should NOT be flagged as overdue!)
        Task completedPastTask = taskService.createTask(testSubject.getId(), "Finished Work", "Done already", Priority.MEDIUM, LocalDate.now().minusDays(1), 20);
        taskService.markTaskComplete(completedPastTask.getId());

        List<Task> overdueList = taskService.getOverdueTasks();
        assertEquals(1, overdueList.size());
        assertEquals("Overdue Lab", overdueList.get(0).getTitle());
    }

    @Test
    @DisplayName("High priority filter returns only pending high priority tasks")
    public void testHighPriorityPendingFilter() throws Exception {
        taskService.createTask(testSubject.getId(), "High Task 1", "Crit", Priority.HIGH, LocalDate.now().plusDays(1), 60);
        Task highDone = taskService.createTask(testSubject.getId(), "High Task 2", "Crit Done", Priority.HIGH, LocalDate.now().plusDays(1), 60);
        taskService.markTaskComplete(highDone.getId());
        taskService.createTask(testSubject.getId(), "Low Task", "Easy", Priority.LOW, LocalDate.now().plusDays(1), 30);

        List<Task> highPending = taskService.getHighPriorityPendingTasks();
        assertEquals(1, highPending.size());
        assertEquals("High Task 1", highPending.get(0).getTitle());
    }
}
