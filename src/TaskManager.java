import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskCount = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (int taskId : tasks.keySet()) {
            Task task = tasks.get(taskId);
            taskList.add(task);
        }
        return taskList;
    }

    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (int epicId : epics.keySet()) {
            Epic epic = epics.get(epicId);
            epicList.add(epic);
        }
        return epicList;
    }

    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (int subtaskId : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    // Удаление всех задач
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        for (Integer index : epics.keySet()) {
            removeEpicById(index);
        }
    }

    // Получение по Id
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    // Создание
    public void createTask(Task task) {
        task.setId(taskCount);
        tasks.put(taskCount, task);
        taskCount++;
    }

    public void createEpic(Epic epic) {
        epic.setId(taskCount);
        epics.put(taskCount, epic);
        taskCount++;
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(taskCount);
        subtasks.put(taskCount, subtask);
        taskCount++;
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
    }

    // Обновление
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Удаление по Id
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtask(subtaskId);
            subtasks.remove(subtaskId);
            System.out.println("Подзадача удалена");
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            for (Subtask subtask : epic.getSubtaskList().values()) {
                subtasks.remove(subtask.getId());
            }

            epic.removeAllSubtasks();
            epics.remove(epicId);
            System.out.println("Эпик удален");
        } else {
            System.out.println("Такого эпика не существует");
        }
    }


    public ArrayList<Subtask> getEpicSubtaskList(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subtask : epic.getSubtaskList().values()) {
            subtaskList.add(subtask);
        }
        return subtaskList;
    }
}
