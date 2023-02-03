package manager;

import models.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InMemoryHistoryManager implements HistoryManager {
    LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        addInformationInHistoryQueue(task);
    }

    private void addInformationInHistoryQueue(Task task) {
        if (history.size() < 10) {
            history.add(task);
        } else {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
