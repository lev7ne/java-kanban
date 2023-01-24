import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private final String NEW = "NEW";
    private final String IN_PROGRESS = "IN_PROGRESS";
    private final String DONE = "DONE";
    private Integer id = 0;
    Map<Integer, Task> taskMap = new HashMap<>();
    Map<Integer, Epic> epicMap = new HashMap<>();
    Map<Integer, Subtask> subtaskMap = new HashMap<>();

    public String getNEW() {
        return NEW;
    }

    public String getIN_PROGRESS() {
        return IN_PROGRESS;
    }

    public String getDONE() {
        return DONE;
    }

    void counterID() { // метод для увеличения счетчика, помогает присваивать всем models.Task уникальный идентификатор
        this.id++;
    }

    // метод для добавления объекта Task в общую TaskMap
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
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            if (pair.getValue().getId().equals(subtask.getEpicId())) {
                pair.getValue().getSubtaskIdList().add(id);
            }
        }
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
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
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
            tasks.add(pair.getValue());
        }
        return tasks;
    }

    List<Epic> getEpics() {
        List<Epic> epics = new ArrayList<>();
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            epics.add(pair.getValue());
        }
        return epics;
    }

    List<Subtask> getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
            subtasks.add(pair.getValue());
        }
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
            if (someSubtask.getEpicId().equals(id)) {
                forReturnSubtaskList.add(someSubtask);
            }
        }
        return forReturnSubtaskList;
    }

    void updateTask(Task task) {
        for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
            if (task.equals(pair.getValue())) {
                pair.getValue().setTitle(task.getTitle());
                pair.getValue().setDescription(task.getDescription());
                pair.getValue().setStatus(task.getStatus());
            }
        }
    }

    void updateEpic(Epic epic) {
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            if (epic.equals(pair.getValue())) {
                pair.getValue().setTitle(epic.getTitle());
                pair.getValue().setDescription(epic.getDescription());
            }
        }
    }

    void updateSubtask(Subtask subtask) {
        for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
            if (subtask.equals(pair.getValue())) {
                pair.getValue().setTitle(subtask.getTitle());
                pair.getValue().setDescription(subtask.getDescription());
                pair.getValue().setStatus(subtask.getStatus());
            }
        }
        updateEpicStatus(subtask.getEpicId());
    }

    void deleteTask(int id) {
        taskMap.remove(id);
    }

    void deleteEpic(int id) {
        epicMap.remove(id);
        for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
            if (pair.getValue().getEpicId().equals(id)) {
                subtaskMap.replace(null, null);
            }
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