import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    void shouldBeEquals0WhenAddEpicToEpicSubtaskList() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, epic.getId(), TaskType.SUBTASK);
        subtask.setId(2);
        epic.addSubtask(epic.getId());
        assertEquals(0, epic.getSubtaskList().size(), "Объект Epic получилось добавить в самого себя в виде подзадачи");
    }
}