import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int taskCount = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public int getTaskCount() {
        return taskCount;
    }

    @Override
    public List<Task> getTaskList() {
        return tasks.keySet().stream().map(this::getTaskById).toList();
    }

    @Override
    public List<Epic> getEpicList() {
        return epics.keySet().stream().map(this::getEpicById).toList();
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return subtasks.keySet().stream().map(this::getSubtaskById).toList();
    }

    // Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.removeAllSubtasks();
            epic.setStatus(getEpicStatus(epic.getId()));
        });

        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });

        epics.clear();
        subtasks.clear();
    }

    // Получение по Id
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            updateHistory(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            updateHistory(subtask);
        }
        updateHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            updateHistory(epic);
        }
        return epic;
    }

    // Создание
    @Override
    public void createTask(Task task) {
        task.setId(taskCount);
        tasks.put(taskCount, task);
        taskCount++;
        if (task.getDuration().toMinutes() != 0 && !isTaskIntersected(task)) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskCount);
        epic.setDuration(Duration.ofMinutes(0));
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
        updateEpicTime(epic.getId());
        if (subtask.getDuration().toMinutes() != 0 && !isTaskIntersected(subtask)) {
            prioritizedTasks.add(subtask);
        }
    }

    // Обновление
    @Override
    public void updateTask(Task task) {
        if (task.getId() > this.taskCount) {
            this.taskCount = task.getId() + 1;
        }
        tasks.put(task.getId(), task);

        prioritizedTasks.remove(task);
        if (task.getDuration().toMinutes() != 0 && !isTaskIntersected(task)) {
            prioritizedTasks.add(task);
        }
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
        updateEpicTime(epic.getId());

        prioritizedTasks.remove(subtask);
        if (subtask.getDuration().toMinutes() != 0 && !isTaskIntersected(subtask)) {
            prioritizedTasks.add(subtask);
        }
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
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.remove(taskId);
            historyManager.remove(taskId);
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
            updateEpicTime(epic.getId());
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
            System.out.println("Подзадача удалена");
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            epic.getSubtaskList().forEach(subtaskId -> {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });

            epics.remove(epicId);
            historyManager.remove(epicId);
            System.out.println("Эпик удален");
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public List<Subtask> getEpicSubtaskList(int epicId) {
        Epic epic = getEpicById(epicId);
        List<Integer> subtaskIds = epic.getSubtaskList();
        List<Subtask> subtaskList = new ArrayList<>();

        subtaskIds.forEach(subtaskId -> {
            Subtask subtask = getSubtaskById(subtaskId);
            subtaskList.add(subtask);
        });

        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private TaskStatus getEpicStatus(int epicId) {
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
                default:
                    progressStatusCount++;
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

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtaskList().isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
        }
        List<Subtask> epicSubtaskList = getEpicSubtaskList(epicId);
        Optional<Subtask> subtaskWithMinStartTime = epicSubtaskList.stream()
                .min(Comparator.comparing(Task::getStartTime));
        Optional<Subtask> subtaskWithMaxEndTime = epicSubtaskList.stream()
                .max(Comparator.comparing(Task::getEndTime));

        if (subtaskWithMaxEndTime.isEmpty() || subtaskWithMinStartTime.isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            return;
        }
        LocalDateTime startTime = subtaskWithMinStartTime.get().getStartTime();
        LocalDateTime endTime = subtaskWithMaxEndTime.get().getEndTime();
        Duration duration = Duration.between(startTime, endTime);
        epic.setDuration(duration);
        epic.setStartTime(subtaskWithMinStartTime.get().getStartTime());
        epic.setEndTime(subtaskWithMaxEndTime.get().getEndTime());
    }

    private void updateHistory(Task task) {
        historyManager.add(task);
    }

    public boolean isTaskIntersected(Task taskToCheck) {
        return prioritizedTasks
                .stream()
                .anyMatch(task -> task.getEndTime().isAfter(taskToCheck.getStartTime()) && taskToCheck.getEndTime().isAfter(task.getStartTime()));
    }
}
