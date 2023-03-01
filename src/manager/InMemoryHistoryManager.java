package manager;

import models.Node;
import models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    class CustomLinkedList { // linkLast и getTasks
        Map<Integer, Node> historyHashMap = new HashMap<>();
        private Node first;
        private Node last;
        private int size = 0;

        public void linkLast(Task task) { // добавить в конец
            final Node l = last;
            final Node newNode = new Node(l, task, null);
            historyHashMap.put(size++, newNode);
            // (null, task, null) ->
            if (l == null) {
                last = newNode;
            } else {
                l.next = newNode;
            }
        }

        public Task getTasks() {
            return null;
        }
    }

    LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        addInformationInHistoryLinkedList(task);
    }

    private void addInformationInHistoryLinkedList(Task task) {
        if (history.size() < 10) {
            history.add(task);
        } else {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}

