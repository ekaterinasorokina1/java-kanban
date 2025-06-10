import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(temp);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }

        Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        Task task2 = new Task("Тест", "Проверить, что последняя задача, которую посмотрели, идет первой в списке", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());

        Task newTask = new Task("Задачи", "Проверить, что при удалении задачи, история очищается", TaskStatus.IN_PROGRESS, TaskType.TASK);
        newTask.setId(1);
        taskManager.updateTask(newTask);
        taskManager.getTaskById(1);
    }

    @Test
    void historyShouldHas2Tasks() {
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "После записи одной и той же задачи история просмотра увеличивается");
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
}