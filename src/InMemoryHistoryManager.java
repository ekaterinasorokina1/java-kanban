import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements  HistoryManager{
    private final List<Task> taskHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        if (taskHistory.isEmpty()) {
            System.out.println("История просмотров пуста");
            return new ArrayList<>();
        } else {
            return taskHistory;
        }
    }
}
