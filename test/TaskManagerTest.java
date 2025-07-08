import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {
    static FileBackedTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(temp);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }

        Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 12, 0));
        taskManager.createTask(task1);

        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 6 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(50), LocalDateTime.of(2025, 6, 19, 2, 0));
        taskManager.createEpic(epicStart);

        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(40), LocalDateTime.of(2025, 6, 19, 14, 0));
        taskManager.createSubtask(subtask1);
    }

    @Test
    void shouldReturnTaskByIdAndInstanceOfTask() {
        Task task1 = taskManager.getTaskById(1);
        assertEquals(1, task1.getId(), "Не возвращает задачу по id 1");
        assertInstanceOf(Task.class, task1, "Не возвращае экземпляр класса Task");
    }

    @Test
    void shouldReturnEpicByIdAndInstanceOfEpic() {
        Epic epicStart = taskManager.getEpicById(2);
        assertEquals(2, epicStart.getId(), "Не возвращает epic по id 2");
        assertInstanceOf(Epic.class, epicStart, "Не возвращае экземпляр класса Epic");
    }

    @Test
    void shouldReturnSubtaskByIdAndInstanceOfSubtask() {
        Subtask subtask = taskManager.getSubtaskById(3);
        assertEquals(3, subtask.getId(), "Не возвращает subtask по id 3");
        assertInstanceOf(Subtask.class, subtask, "Не возвращае экземпляр класса Subtask");
    }

    @Test
    void shouldNotSetIdMoreTaskCount() {
        Task task1 = taskManager.getTaskById(1);
        Task task3 = new Task(task1.getName(), task1.getDescription(), TaskStatus.DONE, TaskType.TASK, Duration.ofMinutes(25), LocalDateTime.of(2025, 6, 19, 17, 30));
        task3.setId(10);
        taskManager.updateTask(task3);
        assertEquals(11, taskManager.getTaskCount(), "При обновлении счетчик не увеличился, что приводит к конфликтам");
    }

    @Test
    void shouldBeAllFieldEqualsWhenAddTask() {
        Task task = taskManager.getTaskById(1);
        assertEquals("Теория", task.getName());
        assertEquals("Изучить теорию спринта", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldBeIdEqualsWhenUpdateSubtask() {
        Subtask subtask1 = taskManager.getSubtaskById(3);
        Subtask subtask2 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.IN_PROGRESS, subtask1.getEpicId(), TaskType.SUBTASK, Duration.ofMinutes(45), LocalDateTime.of(2025, 6, 19, 19, 0));
        subtask2.setId(3);
        taskManager.updateSubtask(subtask2);
        assertEquals(subtask1, subtask2, "Не равны id");
    }

    @Test
    void shouldNotHaveIdAfterRemoveTask() {
        taskManager.removeTaskById(1);
        assertTrue(taskManager.getTaskList().isEmpty(), "Задача не удалилась");
    }

    @Test
    void epicShouldNotHasRemovedSubtask() {
        taskManager.removeSubtaskById(3);
        assertTrue(taskManager.getEpicSubtaskList(2).isEmpty(), "Подзадача не удалилась из эпика");
    }

    @Test
    void shouldNotTaskToBeIntersected() {
        Subtask subtask = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, 2, TaskType.SUBTASK, Duration.ofMinutes(40), LocalDateTime.of(2025, 6, 19, 18, 0));
        assertFalse(taskManager.isTaskIntersected(subtask), "Задача не должна пересекаться, она на несколько часов позже остальных");
    }

    @Test
    void shouldTaskToBeIntersectedWithStartTime() {
        Subtask subtask = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, 2, TaskType.SUBTASK, Duration.ofMinutes(50), LocalDateTime.of(2025, 6, 19, 13, 30));
        assertTrue(taskManager.isTaskIntersected(subtask), "Задача должна пересекаться, тк через 30 минут будет другая");
    }

    @Test
    void shouldTaskToBeIntersected() {
        Subtask subtask = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, 2, TaskType.SUBTASK, Duration.ofMinutes(40), LocalDateTime.of(2025, 6, 19, 14, 30));
        assertTrue(taskManager.isTaskIntersected(subtask), "Задача должна пересекаться с задачей старта 2025.06.19.14:00 с продолжительностью 40 мин");
    }
}
