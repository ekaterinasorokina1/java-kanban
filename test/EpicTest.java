import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import tasks.TaskType;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    static FileBackedTaskManager taskManager;

    @BeforeEach
    void beforeEachAnother() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(temp);

        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }
    }

    @Test
    void shouldBeEquals0WhenAddEpicToEpicSubtaskList() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 10, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        subtask.setId(2);
        epic.addSubtask(epic.getId());
        assertEquals(0, epic.getSubtaskList().size(), "Объект Tasks.Epic получилось добавить в самого себя в виде подзадачи");
    }

    @Test
    void shouldBeEqualsNewStatusToEpic() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 10, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        subtask.setId(2);
        Subtask subtask2 = new Subtask("subtask2", "descrip3", TaskStatus.NEW, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 10, 12, 0));
        subtask.setId(3);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Должен быть статус NEW");
    }

    @Test
    void shouldBeEqualsDoneStatusToEpic() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 10, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.DONE, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        subtask.setId(2);
        Subtask subtask2 = new Subtask("subtask2", "descrip3", TaskStatus.DONE, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 10, 12, 0));
        subtask.setId(3);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Должен быть статус DONE");
    }

    @Test
    void shouldBeEqualsInProgressStatusToEpic() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 10, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.IN_PROGRESS, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        subtask.setId(2);
        Subtask subtask2 = new Subtask("subtask2", "descrip3", TaskStatus.IN_PROGRESS, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 10, 12, 0));
        subtask.setId(3);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Должен быть статус IN_PROGRESS");
    }

    @Test
    void shouldBeEqualsInProgressStatusToEpicWhenHasDoneAndNewStatuses() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 10, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        subtask.setId(2);
        Subtask subtask2 = new Subtask("subtask2", "descrip3", TaskStatus.DONE, epic.getId(), TaskType.SUBTASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 10, 12, 0));
        subtask.setId(3);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Должен быть статус IN_PROGRESS");
    }

    @Test
    void epicShouldHaveSubtask() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(0), LocalDateTime.of(2025, 6, 19, 12, 0));
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, 1, TaskType.SUBTASK, Duration.ofMinutes(50), LocalDateTime.of(2025, 6, 19, 10, 0));
        subtask.setId(2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertTrue(epic.getSubtaskList().contains(subtask.getId()), "Не создалась связь между эпиком и подзадачей");
    }
}