import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File file;
    public static final String DATE_FORMATTER = "dd.MM.yyyy HH:mm";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    private void save() {
        try {
            if (!file.exists()) {
                throw new ManagerSaveException("Файла не существует");
            }

            List<String> listOfTaskString = new ArrayList<>();
            for (Task task : getTaskList()) {
                listOfTaskString.add(task.toString());
            }
            for (Task task : getEpicList()) {
                listOfTaskString.add(task.toString());
            }
            for (Task task : getSubtaskList()) {
                listOfTaskString.add(task.toString());
            }

            Files.writeString(Paths.get(file.toString()), String.join(",\n", listOfTaskString));
        } catch (IOException exception) {
            System.out.println("Произошла ошибка " + exception.getMessage());
        }
    }

    private Task fromString(String value) {
        String[] taskData = value.split(",");
        if (taskData[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(taskData[2], taskData[4], TaskStatus.valueOf(taskData[3]), Integer.parseInt(taskData[7]), TaskType.SUBTASK, Duration.ofMinutes(Integer.parseInt(taskData[5])), LocalDateTime.parse(taskData[6], formatter));
            subtask.setId(Integer.parseInt(taskData[0]));
            updateSubtask(subtask);
            return subtask;
        }
        if (taskData[1].equals("EPIC")) {
            Epic epic = new Epic(taskData[2], taskData[4], TaskStatus.valueOf(taskData[3]), TaskType.EPIC, Duration.ofMinutes(Integer.parseInt(taskData[5])), LocalDateTime.parse(taskData[6], formatter));
            epic.setId(Integer.parseInt(taskData[0]));
            updateEpic(epic);
            return epic;
        } else {
            Task task = new Task(taskData[2], taskData[4], TaskStatus.valueOf(taskData[3]), TaskType.valueOf(taskData[1]), Duration.ofMinutes(Integer.parseInt(taskData[5])), LocalDateTime.parse(taskData[6], formatter));
            task.setId(Integer.parseInt(taskData[0]));
            updateTask(task);
            return task;
        }
    }

    private void loadTasksFromFile() throws IOException {
        List<String> tasks = Files.readAllLines(Paths.get(file.toString()));

        tasks.forEach(this::fromString);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

            if (file.isFile()) {
                fileManager.loadTasksFromFile();
            }

            return fileManager;
        } catch (IOException exception) {
            System.out.println("Произошла ошибка" + exception.getMessage());
            return null;
        }
    }

    public File getFile() {
        return file;
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 0, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("Задачи", "Решить все задачи по спринту", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(50), LocalDateTime.of(2025, 6, 19, 15, 0));
        taskManager.createTask(task2);
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 4 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("ТЗ2", "Проверить дату эпика", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 12, 0));
        taskManager.createSubtask(subtask2);

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
