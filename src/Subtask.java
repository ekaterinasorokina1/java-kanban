public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, TaskType type) {
        super(name, description, status, type);
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
        String[] taskData = new String[]{getId() + "", getType().toString(), getName(), getStatus().toString(), getDescription(), epicId + ""};
        return String.join(",", taskData);
    }
}
