public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new Managers().getDefault();

        Task task1 = new Task("Теория", "Изучить теорию спринта", TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Задачи", "Решить все задачи по спринту", TaskStatus.NEW);
        taskManager.createTask(task2);

        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 4 спринта", TaskStatus.NEW);
        taskManager.createEpic(epicStart);
        Epic epicEnd = new Epic("Завершение ", "Отправить задание в гит для проверки", TaskStatus.NEW);
        taskManager.createEpic(epicEnd);

        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Классы", "Написать классы", TaskStatus.NEW, epicStart.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Тестирование", "Написать проверочный код", TaskStatus.NEW, epicStart.getId());
        taskManager.createSubtask(subtask3);

        getTaskAndPrintHistory(taskManager, task2.getId());
        getEpicAndPrintHistory(taskManager, epicEnd.getId());
        getSubtaskAndPrintHistory(taskManager, subtask1.getId());
        getTaskAndPrintHistory(taskManager, task2.getId());
        getEpicAndPrintHistory(taskManager, epicStart.getId());
        getSubtaskAndPrintHistory(taskManager, subtask2.getId());
        getSubtaskAndPrintHistory(taskManager, subtask1.getId());
        getSubtaskAndPrintHistory(taskManager, subtask3.getId());
        getTaskAndPrintHistory(taskManager, task1.getId());

        taskManager.removeTaskById(task1.getId());
        System.out.println("История после удаления задачи с id 1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        taskManager.removeEpicById(epicStart.getId());
        System.out.println("История после удаления epic (id 3) с 3 subtask id(7,5,6):");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicList()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtaskList(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtaskList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void getTaskAndPrintHistory(TaskManager manager, int taskId) {
        manager.getTaskById(taskId);
        System.out.println("Получили задачу по id: " + taskId);
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void getEpicAndPrintHistory(TaskManager manager, int epicId) {
        manager.getEpicById(epicId);
        System.out.println("Получили epic по id: " + epicId);
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void getSubtaskAndPrintHistory(TaskManager manager, int subtaskId) {
        manager.getSubtaskById(subtaskId);
        System.out.println("Получили subtaskId по id: " + subtaskId);
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}

