import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements  HistoryManager{
    private final Map<Integer, Node<Task>> taskLinkedList = new HashMap<>();
    private Node<Task> tailNode;


    @Override
    public void add(Task task) {
        if (taskLinkedList.containsKey((task.getId()))) {
            removeNode(task.getId());
        }
        Node<Task> newNode = linkLast(task);
        taskLinkedList.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (taskLinkedList.containsKey(id)) {
            removeNode(id);
            taskLinkedList.remove((id));
        }
    }

    @Override
    public List<Task> getHistory() {
        if (taskLinkedList.isEmpty()) {
            System.out.println("История просмотров пуста");
            return new ArrayList<>();
        } else {
            return  getTasks();
        }
    }

    public Node<Task> linkLast(Task task) {
        Node<Task> oldTailNode = tailNode;
        Node<Task> newLastNode = new Node<>(tailNode, task, null);
        tailNode = newLastNode;

        if (oldTailNode != null) {
            oldTailNode.next = newLastNode;
        }
        return newLastNode;
    }

    public List<Task> getTasks() {
        if (taskLinkedList.isEmpty()) {
            System.out.println("История просмотров пуста");
            return new ArrayList<>();
        }

        List<Task> taskHistory = new ArrayList<>();
        Node<Task> taskNode = tailNode;

        while (taskNode != null) {
            taskHistory.add(taskNode.item);
            taskNode = taskNode.prev;
        }
        return taskHistory;
    }

    public void removeNode(Integer taskId) {
        Node<Task> deletedNode = taskLinkedList.get(taskId);
        Node<Task> prevNode = deletedNode.prev;
        Node<Task> nextNode = deletedNode.next;

        if (nextNode == null) {
            tailNode = prevNode;
        } else {
            nextNode.prev = prevNode;
        }
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
    }
}
