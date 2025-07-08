import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void shouldTasksBeEqualWhenSameId() {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 8, 0));
        task1.setId(1);
        Task task2 = new Task("task2", "descrip2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 10, 0));
        task2.setId(1);
        assertEquals(task1, task2, "Не равны, если одинаковые id");
    }

    @Test
    void shouldEpicAndSubtaskBeEqualWhenSameId() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(40), LocalDateTime.of(2025, 6, 19, 12, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, 1, TaskType.SUBTASK, Duration.ofMinutes(35), LocalDateTime.of(2025, 6, 19, 15, 0));
        subtask.setId(1);
        assertEquals(epic, subtask, "Не равны, если одинаковые id");
    }

}