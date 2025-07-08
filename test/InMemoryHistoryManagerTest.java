import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest extends TaskManagerTest {
    static TaskManager taskManager;

    @BeforeEach
    void beforeEachAnother() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(temp);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }
        Task task2 = new Task("Тест", "Проверить, что последняя задача, которую посмотрели, идет первой в списке", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 9, 0));
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());

        Task newTask = new Task("Задачи", "Проверить, что при удалении задачи, история очищается", TaskStatus.IN_PROGRESS, TaskType.TASK, Duration.ofMinutes(35), LocalDateTime.of(2025, 6, 19, 15, 0));
        newTask.setId(1);
        taskManager.updateTask(newTask);
        taskManager.getTaskById(1);
    }

    @Test
    void historyShouldHas1Tasks() {
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "После записи одной и той же задачи история просмотра увеличивается");
    }

    @Test
    void shouldBeEmptyHistory() {
        taskManager.removeTaskById(1);
        taskManager.removeTaskById(2);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "После удаления задач история просмотра не очистилась");
    }

    @Test
    void lastGetTaskShouldBeFirst() {
        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.getFirst().getId(), "Задача в списке не первая");
    }

    @Test
    void taskById2ShouldBeFirstAfterDelete() {
        Task task = new Task("Тест", "Проверить, что последняя задача будет эта после удадления первой", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 5, 19, 9, 0));
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(1);
        taskManager.removeTaskById(1);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.getFirst().getId(), "После удаления задача с id 2 не стала первой");
    }

    @Test
    void shouldNotHaveTaskAfterDelete() {
        Task task = new Task("Тест", "Проверить, что последняя задача будет эта после удадления первой", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 5, 19, 9, 0));
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(1);
        taskManager.removeTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task.getId()), "После удаления задача с id 2 она осталась в истории");
    }

}