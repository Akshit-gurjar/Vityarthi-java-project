package com.studyplanner;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.model.*;
import com.studyplanner.repository.StudySessionRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TaskRepository;
import com.studyplanner.test.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.studyplanner.test.Assertions.*;

/**
 * Integration tests for DatabaseManager and repository CRUD operations.
 */
public class DatabaseCrudTest {

    private static final String TEST_DB = "bin/test_studyplanner.dat";
    private DatabaseManager dbManager;
    private SubjectRepository subjectRepo;
    private TaskRepository taskRepo;
    private StudySessionRepository sessionRepo;

    @BeforeEach
    public void setUp() {
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();

        dbManager = new DatabaseManager(TEST_DB);
        subjectRepo = new SubjectRepository(dbManager);
        taskRepo = new TaskRepository(dbManager);
        sessionRepo = new StudySessionRepository(dbManager);
    }

    @AfterEach
    public void tearDown() {
        dbManager.close();
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();
    }

    @Test
    @DisplayName("Test Subject CRUD operations")
    public void testSubjectCrud() {
        Subject sub = new Subject("Discrete Math", "MATH201", 3, "Logic and Sets");
        Subject saved = subjectRepo.save(sub);
        assertTrue(saved.getId() > 0);

        Optional<Subject> retrieved = subjectRepo.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("MATH201", retrieved.get().getCode());

        retrieved.get().setName("Discrete Mathematics");
        assertTrue(subjectRepo.update(retrieved.get()));

        Optional<Subject> updated = subjectRepo.findById(saved.getId());
        assertEquals("Discrete Mathematics", updated.get().getName());

        assertTrue(subjectRepo.delete(saved.getId()));
        assertFalse(subjectRepo.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Test Task CRUD operations and foreign key relations")
    public void testTaskCrud() {
        Subject sub = subjectRepo.save(new Subject("Operating Systems", "CS301", 4, "OS Concepts"));

        Task task = new Task(sub.getId(), "Thread Synchronization", "Study Mutex and Semaphores",
                Priority.HIGH, LocalDate.now().plusDays(2), TaskStatus.PENDING, 90);
        Task savedTask = taskRepo.save(task);
        assertTrue(savedTask.getId() > 0);

        List<Task> bySub = taskRepo.findBySubjectId(sub.getId());
        assertEquals(1, bySub.size());
        assertEquals("Thread Synchronization", bySub.get(0).getTitle());

        // Update status
        assertTrue(taskRepo.updateStatus(savedTask.getId(), TaskStatus.COMPLETED));
        Optional<Task> refreshed = taskRepo.findById(savedTask.getId());
        assertTrue(refreshed.isPresent());
        assertEquals(TaskStatus.COMPLETED, refreshed.get().getStatus());
    }

    @Test
    @DisplayName("Test StudySession CRUD and retrieval")
    public void testStudySessionCrud() {
        Subject sub = subjectRepo.save(new Subject("Database Systems", "CS201", 3, "SQL and Normalization"));

        StudySession session = new StudySession(sub.getId(), LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(11, 30), "ER Modeling", SessionStatus.SCHEDULED);
        StudySession saved = sessionRepo.save(session);
        assertTrue(saved.getId() > 0);
        assertEquals(90, saved.getDurationMinutes());

        List<StudySession> list = sessionRepo.findByDate(LocalDate.now());
        assertFalse(list.isEmpty());
        assertEquals("ER Modeling", list.get(0).getTopic());
    }
}
