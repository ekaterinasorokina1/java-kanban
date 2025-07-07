import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, TaskType type, int duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            return;
        }
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String[] taskData = new String[]{getId() + "", getType().toString(), getName(), getStatus().toString(), getDescription(), getDuration() + "", getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), epicId + ""};
        return String.join(",", taskData);
    }
}
