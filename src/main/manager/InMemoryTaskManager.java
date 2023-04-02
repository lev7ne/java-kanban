package main.manager;

import main.models.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static main.models.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, Epic> epicMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskMap = new HashMap<>();

    HistoryManager inMemoryHistoryManager = Managers.getDefaultInMemoryHistoryManager();

    protected void counterID() {
        this.id++;
    }

    protected void updateEpicStatus(Integer epicId) {
        int counterNew = 0;
        int counterInProgress = 0;
        int counterDone = 0;
        List<Integer> subtasks = epicMap.get(epicId).getSubtaskIdList();
        if (subtasks.isEmpty()) {
            epicMap.get(epicId).setStatus(NEW);
        } else {
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
    }

    protected void updateEpicDurationAndStartTimeAndEndTime(Integer epicId) {
        Epic epic = epicMap.get(epicId);
        List<Integer> subtasks = epic.getSubtaskIdList();
        Duration epicDuration = Duration.ZERO;
        if (epic.getSubtaskIdList().isEmpty()) {
            epic.setDuration(epicDuration);
        } else {
            Instant epicStartTime = subtaskMap.get(epic.getSubtaskIdList().get(0)).getStartTime();
            Instant epicEndTime = subtaskMap.get(epic.getSubtaskIdList().get(0)).getEndTime();
            for (Integer subtaskId : subtasks) {
                Subtask someSubtask = subtaskMap.get(subtaskId);
                epicDuration = epicDuration.plus(someSubtask.getDuration());
                if (someSubtask.getStartTime().isBefore(epicStartTime)) {
                    epicStartTime = someSubtask.getStartTime();
                }
                if (someSubtask.getEndTime().isAfter(epicEndTime)) {
                    epicEndTime = someSubtask.getEndTime();
                }
            }
            epic.setStartTime(epicStartTime);
            epic.setDuration(epicDuration);
            epic.setEndTime(epicEndTime);
        }
    }

    protected void validateTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        List<Task> tasks = new ArrayList<>(getPrioritizedTasks());
        for (Task anyTask : tasks) {
            if (anyTask.getId().equals(task.getId())) {
                continue;
            }
            if (anyTask.getStartTime() == null) {
                break;
            }
            boolean isFlag3 = (task.getStartTime().isAfter(anyTask.getEndTime()) && task.getEndTime().isAfter(anyTask.getEndTime()))
                    && (task.getStartTime().isBefore(anyTask.getStartTime()) && task.getEndTime().isBefore(anyTask.getStartTime()));
            if (!isFlag3) {
                throw new ManagerValidateTaskException("Попытка добавить задачу, которая пересекается по времени с уже существующей задачей.");
            }
        }
    }

    public Set<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                if (task1.getStartTime() == null && task2.getStartTime() == null) {
                    return task1.getId().compareTo(task2.getId());
                } else if (task1.getStartTime() == null || task2.getStartTime() == null) {
                    if (task1.getStartTime() == null) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
                    return -1;
                } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        Set<Task> prioritizedTasks = new TreeSet<>(comparator);
        prioritizedTasks.addAll(taskMap.values());
        prioritizedTasks.addAll(subtaskMap.values());
        return prioritizedTasks;
    }

    @Override
    public int createTask(Task task) {
        counterID();
        validateTask(task);
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
        validateTask(subtask);
        subtask.setId(id);
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtaskIdList().add(id);
            updateEpicStatus(subtask.getEpicId());
            updateEpicDurationAndStartTimeAndEndTime(subtask.getEpicId());
        }
        return subtask.getId();
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
        inMemoryHistoryManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Task getEpic(Integer id) {
        inMemoryHistoryManager.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public Task getSubtask(Integer id) {
        inMemoryHistoryManager.add(subtaskMap.get(id));
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
        validateTask(task);
        Task someTask = taskMap.get(task.getId());
        if (someTask == null) {
            return;
        }
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic someEpic = epicMap.get(epic.getId());
        if (someEpic == null) {
            return;
        }
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        validateTask(subtask);
        Subtask someSubtask = subtaskMap.get(subtask.getId());
        if (someSubtask == null) {
            return;
        }
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicDurationAndStartTimeAndEndTime(subtask.getEpicId());
    }

    @Override
    public void deleteTask(int id) {
        taskMap.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicMap.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subId : epic.getSubtaskIdList()) {
            subtaskMap.remove(subId);
            inMemoryHistoryManager.remove(subId);
        }
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        int epicId = subtaskMap.get(id).getEpicId();
        subtaskMap.remove(id);
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().getSubtaskIdList().remove((Integer) id);
        }
        updateEpicStatus(epicId);
        updateEpicDurationAndStartTimeAndEndTime(epicId);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
            inMemoryHistoryManager.remove(pair.getKey());
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            for (Integer subtaskId : pair.getValue().getSubtaskIdList()) {
                inMemoryHistoryManager.remove(subtaskId);
            }
            inMemoryHistoryManager.remove(pair.getKey());
        }
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
            inMemoryHistoryManager.remove(pair.getKey());
        }
        subtaskMap.clear();
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            pair.getValue().setStatus(NEW);
            pair.getValue().getSubtaskIdList().clear();
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}