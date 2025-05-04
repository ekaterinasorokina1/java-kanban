import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task>getTaskList();
    ArrayList<Epic> getEpicList();
    ArrayList<Subtask> getSubtaskList();
    // Удаление всех задач
    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    // Получение по Id
    Task getTaskById(int taskId);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    // Создание
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    // Обновление
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // Удаление по Id
    void removeTaskById(int taskId);

    void removeSubtaskById(int subtaskId);

    void removeEpicById(int epicId);

    ArrayList<Subtask> getEpicSubtaskList(int epicId);

    TaskStatus getEpicStatus(int epicId);

    ArrayList<Task> getHistory();

    void updateHistory(Task task);

    int getTaskCount();
}
