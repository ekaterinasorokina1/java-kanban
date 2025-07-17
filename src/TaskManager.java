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
    boolean createTask(Task task);

    void createEpic(Epic epic);

    boolean createSubtask(Subtask subtask);

    // Обновление
    boolean updateTask(Task task);

    boolean updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    // Удаление по Id
    void removeTaskById(int taskId);

    void removeSubtaskById(int subtaskId);

    void removeEpicById(int epicId);

    List<Subtask> getEpicSubtaskList(int epicId);

    List<Task> getHistory();

    int getTaskCount();

    List<Task> getPrioritizedTasks();
}
