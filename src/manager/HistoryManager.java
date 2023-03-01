package manager;

import models.Task;

import java.util.List;
import java.util.Queue;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}