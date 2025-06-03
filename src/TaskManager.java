import java.util.List;

public interface TaskManager {
    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

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

    List<Subtask> getEpicSubtaskList(int epicId);

    List<Task> getHistory();

    int getTaskCount();
}
