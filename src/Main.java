public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

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
        Subtask subtask3 = new Subtask("Тестирование", "Написать проверочный код", TaskStatus.NEW, epicEnd.getId());
        taskManager.createSubtask(subtask3);


        System.out.println("Список созданных задач: " + taskManager.getTaskList());
        System.out.println("Список созданных эпиков: " + taskManager.getEpicList());
        System.out.println("Список созданных подзадач: " + taskManager.getSubtaskList());


        Task task3 = new Task("Задачи", "Решить все задачи по спринту", TaskStatus.IN_PROGRESS);
        task3.setId(task2.getId());
        taskManager.updateTask(task3);
        System.out.println("Список задач после обновления: " + taskManager.getTaskList());

        Subtask subtask4 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.DONE, subtask1.getEpicId());
        subtask4.setId(subtask1.getId());
        taskManager.updateSubtask(subtask4);
        System.out.println("Список подзадач в эпике после обновления подзадач: "+  taskManager.getEpicSubtaskList(epicStart.getId()));
        System.out.println("Статус эпика после обновления подзадачи: " + epicStart.getStatus());

        taskManager.removeTaskById(task1.getId());
        System.out.println("Список задач после удаления одной задачи: " + taskManager.getTaskList());

        taskManager.removeSubtaskById(subtask3.getId());
        System.out.println("Список подзадач после удаления одной подзадачи: " + taskManager.getSubtaskList());
        System.out.println("Список подзадач в эпике после удаления одной подзадачи: " + taskManager.getEpicSubtaskList(subtask3.getEpicId()));

        taskManager.removeSubtaskById(subtask4.getId());
        System.out.println("Статус эпика после обновления подзадачи: " + epicStart.getStatus());
        System.out.println("Список подзадач в эпике после удаления одной подзадачи: " + taskManager.getEpicSubtaskList(subtask4.getEpicId()));

        taskManager.removeEpicById(epicStart.getId());
        System.out.println("Список подзадач после удаления эпика: " + taskManager.getSubtaskList());
    }
}

