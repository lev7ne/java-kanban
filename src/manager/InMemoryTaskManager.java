package manager;

import models.*;

import java.util.*;

import static models.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;

    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();

    HistoryManager hm = Managers.getDefaultHistory();

    public void counterID() {
        this.id++;
    }

    @Override
    public int createTask(Task task) {
        counterID();
        task.setId(id);
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        counterID();
        epic.setId(id);
        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
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

    //это вспомогательный метод, его в интерфейс не добавляю
    private void updateEpicStatus(Integer epicId) {
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

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public Task getTask(Integer id) {
        hm.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Task getEpic(Integer id) {
        hm.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public Task getSubtask(Integer id) {
        hm.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer id) {
        ArrayList<Subtask> forReturnSubtaskList = new ArrayList<>();
        for (Integer subtaskId : epicMap.get(id).getSubtaskIdList()) {
            Subtask someSubtask = subtaskMap.get(subtaskId);
            forReturnSubtaskList.add(someSubtask);
        }
        return forReturnSubtaskList;
    }

    @Override
    public void updateTask(Task task) {
        Task someTask = taskMap.get(task.getId());
        if (someTask == null) {
            return;
        }
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic someEpic = epicMap.get(epic.getId());
        if (someEpic == null) { // перед заменой, проверяем не в пустом ли эпике, пытаемся обновить инфо
            return;
        }
        someEpic.setTitle(epic.getTitle());
        someEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask someSubtask = subtaskMap.get(subtask.getId());
        if (someSubtask == null) {
            return;
        }
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTask(int id) {
        taskMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicMap.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subId : epic.getSubtaskIdList()) {
            subtaskMap.remove(subId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtaskMap.get(id).getEpicId();
        subtaskMap.remove(id);
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().getSubtaskIdList().remove((Integer) id);
        }
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTasks() {
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtaskMap.clear();
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().setStatus(NEW);
            pair.getValue().getSubtaskIdList().clear();
        }
    }

    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }
}