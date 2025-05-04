import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int taskCount = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public int getTaskCount() {
        return taskCount;
    }

    @Override
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (int taskId : tasks.keySet()) {
            Task task = getTaskById(taskId);
            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> epicList = new ArrayList<>();
        for (int epicId : epics.keySet()) {
            Epic epic = getEpicById(epicId);
            epicList.add(epic);
        }
        return epicList;
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (int subtaskId : subtasks.keySet()) {
            Subtask subtask = getSubtaskById(subtaskId);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    // Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            epic.setStatus(getEpicStatus(epic.getId()));
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Получение по Id
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        updateHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        updateHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        updateHistory(epic);
        return epic;
    }

    // Создание
    @Override
    public void createTask(Task task) {
        task.setId(taskCount);
        tasks.put(taskCount, task);
        taskCount++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCount);
        epics.put(taskCount, epic);
        taskCount++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(taskCount);
        subtasks.put(taskCount, subtask);
        taskCount++;
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        epic.setStatus(getEpicStatus(epic.getId()));
    }

    // Обновление
    @Override
    public void updateTask(Task task) {
        if(task.getId() > this.taskCount) {
            this.taskCount = task.getId() + 1;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();

        if (subtaskId > this.taskCount) {
           this.taskCount = subtaskId + 1;
        }
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtaskId);
        epic.setStatus(getEpicStatus(epic.getId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic.getId() > this.taskCount) {
            this.taskCount = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
    }

    // Удаление по Id
    @Override
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtask(subtaskId);
            epic.setStatus(getEpicStatus(epic.getId()));
            subtasks.remove(subtaskId);
            System.out.println("Подзадача удалена");
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> subtaskIds = epic.getSubtaskList();

            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }

            epics.remove(epicId);
            System.out.println("Эпик удален");
        } else {
            System.out.println("Такого эпика не существует");
        }
    }


    @Override
    public ArrayList<Subtask> getEpicSubtaskList(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Integer> subtaskIds = epic.getSubtaskList();
        ArrayList<Subtask> subtaskList = new ArrayList<>();

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = getSubtaskById(subtaskId);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    @Override
    public TaskStatus getEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtaskList().isEmpty()) {
            return TaskStatus.NEW;
        }
        int newStatusCount = 0;
        int doneStatusCount = 0;
        int progressStatusCount = 0;

        for (int subtaskId : epic.getSubtaskList()) {
            Subtask subtask = subtasks.get(subtaskId);

            switch (subtask.getStatus()) {
                case NEW:
                    newStatusCount++;
                    break;
                case DONE:
                    doneStatusCount++;
                    break;
                default: progressStatusCount++;
            }
        }
        int subtaskSize = epic.getSubtaskList().size();

        if (progressStatusCount > 0) {
            return TaskStatus.IN_PROGRESS;
        }
        if (doneStatusCount > 0 && doneStatusCount == subtaskSize) {
            return TaskStatus.DONE;
        }
        if (newStatusCount > 0 && newStatusCount == subtaskSize) {
            return TaskStatus.NEW;
        }
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateHistory(Task task) {
        historyManager.add(task);
    }
}
