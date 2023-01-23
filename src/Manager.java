import models.Epic;
import models.Subtask;
import models.Task;

import java.util.HashMap;
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
    void createTask(Task task) {
        counterID();
        task.setId(id);
        taskMap.put(task.getId(), task);
    }

    void createEpic(Epic epic) {
        counterID();
        epic.setId(id);
        epicMap.put(epic.getId(), epic);
    }

    void createSubtask(Subtask subtask) {
        counterID();
        subtask.setId(id);
        int i = 0;
        for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
            if (pair.getValue().getId().equals(subtask.getEpicId())) {
                pair.getValue().getSubtaskIdList().add(id);
                i = pair.getKey();
            }
        }
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(i);
    }

    void updateEpicStatus(Integer epicId) {
        int counterNew = 0;
        int counterInProgress = 0;
        int counterDone = 0;
        for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
            if (pair.getValue().getEpicId().equals(epicId)) {
                if (pair.getValue().getStatus().equals(NEW)) {
                    counterNew++;
                }
                if (pair.getValue().getStatus().equals(IN_PROGRESS)) {
                    counterInProgress++;
                }
                if (pair.getValue().getStatus().equals(DONE)) {
                    counterDone++;
                }
            }
        }
        epicMap.get(epicId).setStatus(counterNew > 0 && counterInProgress == 0 && counterDone == 0 ? NEW :
                counterNew == 0 && counterInProgress == 0 && counterDone > 0 ? DONE : IN_PROGRESS);
    }

}