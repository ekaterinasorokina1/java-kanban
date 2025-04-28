import java.util.HashMap;

public class Epic extends Task {
    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(HashMap<Integer, Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic (String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        manageStatus();
    }


    public void manageStatus() {
        if (subtaskList.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }
        int newStatusCount = 0;
        int doneStatusCount = 0;
        int progressStatusCount = 0;

        for (Subtask subtask : subtaskList.values()) {
            switch (subtask.getStatus()) {
                case NEW:
                    newStatusCount++;
                    break;
                case DONE:
                    doneStatusCount++;
                    break;
                default: progressStatusCount++;
            }
        }
        if (progressStatusCount > 0) {
            setStatus(TaskStatus.IN_PROGRESS);
        }
        if (doneStatusCount > 0 && doneStatusCount == subtaskList.size()) {
            setStatus(TaskStatus.DONE);
            return;
        }
        if (newStatusCount > 0 && newStatusCount == subtaskList.size()) {
            setStatus(TaskStatus.NEW);
            return;
        }
        setStatus(TaskStatus.IN_PROGRESS);
    }

    public void removeSubtask(int subtaskId) {
        subtaskList.remove(subtaskId);
        manageStatus();
    }

    public void removeAllSubtasks() {
        subtaskList.clear();
    }
}
