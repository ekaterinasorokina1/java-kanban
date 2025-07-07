import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest {
    static FileBackedTaskManager taskManager;

    @BeforeEach
    void beforeEachAnother() {
        try {
            File temp = File.createTempFile("temp", ".csv");
            taskManager = FileBackedTaskManager.loadFromFile(temp);

            Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW, TaskType.TASK, 50, LocalDateTime.of(2025, 6, 19, 12, 0));
            taskManager.createTask(task1);
            Task task2 = new Task("Задачи", "Решить все задачи по спринту", TaskStatus.NEW, TaskType.TASK, 40, LocalDateTime.of(2025, 6, 19, 10, 0));
            taskManager.createTask(task2);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
        }
    }

    @Test
    void isFileExist() {
        File file = taskManager.getFile();
        assertTrue(file.exists(), "Файл не создан");
    }

    @Test
    void isTasksSaved() {
        try {
            File file = taskManager.getFile();
            List<String> tasksInStr = Files.readAllLines(Paths.get(file.getPath()));
            assertEquals(2, tasksInStr.size(), "Задачи не записались в файл");
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при получении файла" + exception.getMessage());
        }
    }

    @Test
    void isLoadedTaskAfterCreate() {
        FileBackedTaskManager taskManagerTest = FileBackedTaskManager.loadFromFile(taskManager.getFile());
        assertEquals(2, taskManagerTest.getTaskList().size(), "Не получилось выгрузить задачи из файла");
    }

    @Test
    public void testExceptionWhileLoadFromFile() {
        Assertions.assertDoesNotThrow(() -> {
            File temp = File.createTempFile("temp1", ".csv");
            FileBackedTaskManager.loadFromFile(temp);
        }, "Ошибка при чтении файла");
    }
}
