import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = new Managers().getDefault();
        Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        Task newTask = new Task("Задачи", "Решить все задачи по спринту", TaskStatus.IN_PROGRESS);
        newTask.setId(1);
        taskManager.updateTask(newTask);
        taskManager.getTaskById(1);


    }

    @Test
    void shouldBeSize2OfHistory() {
        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void shouldBeNotEqualsAfterUpdateTask() {
        ArrayList<Task> history = taskManager.getHistory();
        assertNotEquals(history.get(0).getName(), history.get(1).getName(), "После обновления HistoryManager не охраняет предыдущую версию задачи и её данных");
        assertNotEquals(history.get(0).getDescription(), history.get(1).getDescription(),"После обновления HistoryManager не охраняет предыдущую версию задачи и её данных");
        assertNotEquals(history.get(0).getStatus(), history.get(1).getStatus(), "После обновления HistoryManager не охраняет предыдущую версию задачи и её данных");
    }

}