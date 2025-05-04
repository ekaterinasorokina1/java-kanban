import java.util.ArrayList;

public class InMemoryHistoryManager implements  HistoryManager{
    private final ArrayList<Task> taskHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        if (taskHistory.isEmpty()) {
            System.out.println("История просмотров пуста");
            return new ArrayList<>();
        } else {
            return taskHistory;
        }
    }
}
