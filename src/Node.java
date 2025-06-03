
public class Node<T> {
    private T item;
    Node<T> next;
    Node<T> prev;

    Node(Node<T> prev, T element, Node<T> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}