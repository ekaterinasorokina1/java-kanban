import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void shouldTasksBeEqualWhenSameId() {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK);
        task1.setId(1);
        Task task2 = new Task("task2", "descrip2", TaskStatus.NEW, TaskType.TASK);
        task2.setId(1);
        assertEquals(task1, task2, "Не равны, если одинаковые id");
    }

    @Test
    void shouldEpicAndSubtaskBeEqualWhenSameId() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, 1, TaskType.SUBTASK);
        subtask.setId(1);
        assertEquals(epic, subtask, "Не равны, если одинаковые id");
    }

}