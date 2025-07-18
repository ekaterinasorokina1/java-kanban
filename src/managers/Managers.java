package managers;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        File file = new File("taskStore.csv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Файл не удалось создать");
        }

        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
