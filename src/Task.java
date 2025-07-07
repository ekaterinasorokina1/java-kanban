import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private TaskStatus status;
    private final TaskType type;
    private int duration;
    private LocalDateTime startTime;

    public Task(String name, String description, TaskStatus status, TaskType type, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.duration = duration;
        this.startTime = startTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public String toString() {
        String[] taskData = new String[]{id + "", type.toString(), name, status.toString(), description, duration + "", startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))};
        return String.join(",", taskData);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(Duration.ofMinutes(duration));
    }
}
