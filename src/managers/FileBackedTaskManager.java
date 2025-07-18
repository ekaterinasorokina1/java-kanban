package managers;

import tasks.*;

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
    public boolean createTask(Task task) {
        boolean isTaskIntersected = super.createTask(task);
        save();
        return isTaskIntersected;
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        boolean isTaskIntersected = super.createSubtask(subtask);
        save();
        return isTaskIntersected;
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
    public boolean updateTask(Task task) {
        boolean isTaskIntersected = super.updateTask(task);
        save();
        return isTaskIntersected;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean isTaskIntersected = super.updateSubtask(subtask);
        save();
        return isTaskIntersected;
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


}
