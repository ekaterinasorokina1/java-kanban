import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskList = new ArrayList<>();

    public Epic (String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Integer> subtaskList) {
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
}
