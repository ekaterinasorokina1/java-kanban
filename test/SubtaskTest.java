import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldBeEquals1WhenAddEpicIdToSubtask() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 12, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, 1, TaskType.SUBTASK, Duration.ofMinutes(50), LocalDateTime.of(2025, 6, 19, 10, 0));
        subtask.setId(2);
        subtask.setEpicId(2);
        assertEquals(1, subtask.getEpicId(), "Объект Subtask получилось сделать своим же эпиком");
    }
}