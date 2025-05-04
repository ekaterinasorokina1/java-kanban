import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldReturnInstanceOfTaskManager() {
        TaskManager taskManager = new Managers().getDefault();

        assertInstanceOf(TaskManager.class, taskManager, "Не возвращает к работе экземпляр класса TaskManager");
    }

    @Test
    void shouldReturnInstanceOfHistoryManager() {
        HistoryManager historyManager = new Managers().getDefaultHistory();

        assertInstanceOf(HistoryManager.class, historyManager, "Не возвращает к работе экземпляр класса HistoryManager");
    }
}