package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;
import java.util.Queue;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task getTask(Integer id);

    Task getEpic(Integer id);

    Task getSubtask(Integer id);

    List<Subtask> getEpicSubtasks(Integer id);

    void updateTask(Task task);

    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    List<Task> getHistory();
}