import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldBeEquals1WhenAddEpicIdToSubtask() {
        Epic epic = new Epic("epic", "descrip1", TaskStatus.NEW, TaskType.EPIC);
        epic.setId(1);
        Subtask subtask = new Subtask("subtask", "descrip2", TaskStatus.NEW, 1, TaskType.SUBTASK);
        subtask.setId(2);
        subtask.setEpicId(2);
        assertEquals(1, subtask.getEpicId(), "Объект Subtask получилось сделать своим же эпиком");
    }
}