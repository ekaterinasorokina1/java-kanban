import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, TaskType type, int duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
    }

    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(List<Integer> subtaskList) {
        this.subtaskList = subtaskList;
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId == this.getId()) {
            return;
        }
        if (subtaskList.contains(subtaskId)) {
            return;
        }
        subtaskList.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskList.remove((Integer) subtaskId);
    }

    public void removeAllSubtasks() {
        subtaskList.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
