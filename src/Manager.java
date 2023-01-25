import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    public static final String NEW = "NEW";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String DONE = "DONE";
    private Integer id = 0;
    Map<Integer, Task> taskMap = new HashMap<>();
    Map<Integer, Epic> epicMap = new HashMap<>();
    Map<Integer, Subtask> subtaskMap = new HashMap<>();

    void counterID() { // метод для увеличения счетчика, помогает присваивать всем models.Task уникальный идентификатор
        this.id++;
    }

    int createTask(Task task) {
        counterID();
        task.setId(id);
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    int createEpic(Epic epic) {
        counterID();
        epic.setId(id);
        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    int createSubtask(Subtask subtask) {
        counterID();
        subtask.setId(id);
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIdList().add(id);
            updateEpicStatus(subtask.getEpicId());
        }
        return subtask.getId();
    }

    void updateEpicStatus(Integer epicId) {
        int counterNew = 0;
        int counterInProgress = 0;
        int counterDone = 0;
        List<Integer> subtasks = epicMap.get(epicId).getSubtaskIdList();
        for (Integer subtaskId : subtasks) {
            Subtask someSubtask = subtaskMap.get(subtaskId);
            if (someSubtask.getStatus().equals(NEW)) {
                counterNew++;
            }
            if (someSubtask.getStatus().equals(IN_PROGRESS)) {
                counterInProgress++;
            }
            if (someSubtask.getStatus().equals(DONE)) {
                counterDone++;
            }
        }
        epicMap.get(epicId).setStatus(counterNew > 0 && counterInProgress == 0 && counterDone == 0 ? NEW :
                counterNew == 0 && counterInProgress == 0 && counterDone > 0 ? DONE : IN_PROGRESS);
    }

    List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>(subtaskMap.values());
        return tasks;
    }

    List<Epic> getEpics() {
        List<Epic> epics = new ArrayList<>(epicMap.values());
        return epics;
    }

    List<Subtask> getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>(subtaskMap.values());
        return subtasks;
    }

    Task getTask(Integer id) {
        return taskMap.get(id);
    }

    Task getEpic(Integer id) {
        return epicMap.get(id);
    }

    Task getSubtask(Integer id) {
        return subtaskMap.get(id);
    }

    List<Subtask> getEpicSubtasks(Integer id) {
        ArrayList<Subtask> forReturnSubtaskList = new ArrayList<>();
        for (Integer subtaskId : epicMap.get(id).getSubtaskIdList()) {
            Subtask someSubtask = subtaskMap.get(subtaskId);
            forReturnSubtaskList.add(someSubtask);
        }
        return forReturnSubtaskList;
    }

    void updateTask(Task task) {
        Task someTask = taskMap.get(task.getId());
        if (someTask == null) {
            return;
        }
        taskMap.put(task.getId(), task);

    }

    void updateEpic(Epic epic) {
        Epic someEpic = epicMap.get(epic.getId());
        if (someEpic == null) { // перед заменой, проверяем не в пустом ли эпике, пытаемся обновить инфо
            return;
        }
        someEpic.setTitle(epic.getTitle());
        someEpic.setDescription(epic.getDescription());
    }

    void updateSubtask(Subtask subtask) {
        Subtask someSubtask = subtaskMap.get(subtask.getId());
        if (someSubtask == null) {
            return;
        }
        if (subtaskMap.containsKey(subtask.getId())) {
            subtaskMap.put(subtask.getId(), subtask);
        }
        updateEpicStatus(subtask.getEpicId());
    }

    void deleteTask(int id) {
        taskMap.remove(id);
    }

    void deleteEpic(int id) {
        Epic epic = epicMap.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subId : epic.getSubtaskIdList()) {
            subtaskMap.remove(subId);
        }
    }

    void deleteSubtask(int id) {
        int epicId = subtaskMap.get(id).getEpicId();
        subtaskMap.remove(id);
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().getSubtaskIdList().remove((Integer) id);
        }
        updateEpicStatus(epicId);
    }

    void deleteTasks() {
        taskMap.clear();
    }

    void deleteEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    void deleteSubtasks() {
        subtaskMap.clear();
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().setStatus(NEW);
            pair.getValue().getSubtaskIdList().clear();
        }
    }

}