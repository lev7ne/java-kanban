package manager;

import models.Node;
import models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    CustomLinkedList history = new CustomLinkedList();

    class CustomLinkedList {
        Map<Integer, Node> historyHashMap = new HashMap<>();
        private Node first;
        private Node last;


        public void linkLast(Task task) {
            final Node l = last;
            final Node newNode = new Node(l, task, null);
            historyHashMap.put(task.getId(), newNode);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }
        }

        public List<Task> getTasks() {
            List<Task> historyArrayList = new ArrayList<>();
            Node n = first;
            while (n != null) {
                historyArrayList.add(n.task);
                n = n.next;
            }
            return historyArrayList;
        }

        private void removeNode(Node node) { // NPE выбивал в этом методе постоянно, помогла вот эта проверка history.historyHashMap.containsKey(task.getId())
                                             // в методе addInformationInHistoryLinkedList(Task task)
            
            if (node.prev == null) { // если нода оказывается первой
                Node next = node.next;
                next.prev = null;
                first = next;
            } else if (node.next == null) { // если нода оказывается последней
                Node prev = node.prev;
                prev.next = null;
                last = prev;
            } else if (node.prev != null && node.next != null) { // если нода в середине
                Node prev = node.prev;
                Node next = node.next;
                prev.next = next;
                next.prev = prev;
            } else if (node.prev == null && node.next == null) { // если нода единственная
                first = null;
                last = null;
            }
        }

        public void remove(int id) {
            removeNode(historyHashMap.remove(id)); // постоянно забываю, что метод remove() возвращает значение, поэтому оставлю тут этот коммент
        }
    }

    @Override
    public void add(Task task) {
        addInformationInHistoryLinkedList(task);
    }

    private void addInformationInHistoryLinkedList(Task task) {
        if (history.historyHashMap.containsKey(task.getId())) {
            history.remove(task.getId());
        }
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
}


