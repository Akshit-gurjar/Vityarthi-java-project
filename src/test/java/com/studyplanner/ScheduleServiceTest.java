package com.studyplanner;

import com.studyplanner.database.DatabaseManager;
import com.studyplanner.exception.InvalidInputException;
import com.studyplanner.model.SessionStatus;
import com.studyplanner.model.StudySession;
import com.studyplanner.model.Subject;
import com.studyplanner.repository.StudySessionRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.service.ScheduleService;
import com.studyplanner.service.SubjectService;
import com.studyplanner.test.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.studyplanner.test.Assertions.*;

/**
 * Unit tests for ScheduleService timing validation and conflict detection.
 */
public class ScheduleServiceTest {

    private static final String TEST_DB = "bin/test_scheduleservice.dat";
    private DatabaseManager dbManager;
    private ScheduleService scheduleService;
    private Subject testSubject;

    @BeforeEach
    public void setUp() throws Exception {
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();

        dbManager = new DatabaseManager(TEST_DB);
        SubjectRepository subjectRepo = new SubjectRepository(dbManager);
        StudySessionRepository sessionRepo = new StudySessionRepository(dbManager);
        SubjectService subjectService = new SubjectService(subjectRepo);

        scheduleService = new ScheduleService(sessionRepo, subjectService);
        testSubject = subjectService.createSubject("Software Engineering", "CS305", 3, "Agile, Design Patterns");
    }

    @AfterEach
    public void tearDown() {
        dbManager.close();
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();
    }

    @Test
    @DisplayName("createSession should persist a valid study session")
    public void testCreateSessionSuccess() throws Exception {
        StudySession session = scheduleService.createSession(
                testSubject.getId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Design Patterns Chapter"
        );

        assertNotNull(session);
        assertTrue(session.getId() > 0);
        assertEquals(120, session.getDurationMinutes());
        assertEquals(SessionStatus.SCHEDULED, session.getStatus());
    }

    @Test
    @DisplayName("createSession should reject invalid time ranges where end <= start")
    public void testInvalidTimeRange() {
        assertThrows(InvalidInputException.class, () -> {
            scheduleService.createSession(
                    testSubject.getId(),
                    LocalDate.now(),
                    LocalTime.of(14, 0),
                    LocalTime.of(13, 0),
                    "Time Paradox"
            );
        });
    }

    @Test
    @DisplayName("findConflicts should accurately detect overlapping study sessions on the same date")
    public void testConflictDetection() throws Exception {
        LocalDate date = LocalDate.now().plusDays(2);

        // Existing session: 14:00 - 16:00
        scheduleService.createSession(testSubject.getId(), date, LocalTime.of(14, 0), LocalTime.of(16, 0), "Session 1");

        // Overlapping candidate: 15:00 - 17:00
        StudySession candidateOverlap = new StudySession(testSubject.getId(), date, LocalTime.of(15, 0), LocalTime.of(17, 0), "Overlap", SessionStatus.SCHEDULED);
        List<StudySession> conflicts = scheduleService.findConflicts(candidateOverlap);
        assertEquals(1, conflicts.size());
        assertEquals("Session 1", conflicts.get(0).getTopic());

        // Non-overlapping candidate: 16:00 - 18:00 (exact boundary touch is allowed)
        StudySession nonOverlap = new StudySession(testSubject.getId(), date, LocalTime.of(16, 0), LocalTime.of(18, 0), "Next Block", SessionStatus.SCHEDULED);
        List<StudySession> noConflicts = scheduleService.findConflicts(nonOverlap);
        assertTrue(noConflicts.isEmpty());
    }
}
