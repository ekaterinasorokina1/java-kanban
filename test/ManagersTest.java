import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldReturnInstanceOfTaskManager() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            TaskManager taskManager = FileBackedTaskManager.loadFromFile(temp);
            assertInstanceOf(TaskManager.class, taskManager, "Не возвращает к работе экземпляр класса TaskManager");
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }
    }

    @Test
    void shouldReturnInstanceOfHistoryManager() {
        HistoryManager historyManager = new Managers().getDefaultHistory();

        assertInstanceOf(HistoryManager.class, historyManager, "Не возвращает к работе экземпляр класса HistoryManager");
    }
}