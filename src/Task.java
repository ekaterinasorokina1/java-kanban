import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private final String name;
    private final String description;
    private TaskStatus status;
    private final TaskType type;

    public Task(String name, String description, TaskStatus status, TaskType type) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
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
        String[] taskData = new String[]{id + "", type.toString(), name, status.toString(), description};
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
}
