package managers;

import com.google.gson.Gson;
import models.Epic;
import models.Subtask;
import models.Task;
import servers.KVTaskClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    Gson json;

    HttpTaskManager(int port) {
        kvTaskClient = new KVTaskClient(port);
        json = Managers.getGson();
        load();
    }

    @Override
    public void save() {
        String jsonTasks = json.toJson(new ArrayList<>(taskMap.values()));
        kvTaskClient.put("tasks", jsonTasks);
        String jsonSubtasks = json.toJson(new ArrayList<>(subtaskMap.values()));
        kvTaskClient.put("subtasks", jsonSubtasks);
        String jsonEpic = json.toJson(new ArrayList<>(epicMap.values()));
        kvTaskClient.put("epics", jsonEpic);

        List<Integer> historyId = new ArrayList<>(getHistory()).stream().map(Task::getId).collect(Collectors.toList());
        String jsonHistory = json.toJson(historyId);
        kvTaskClient.put("history", jsonHistory);
    }

    private void load() {
        try {
            List<Task> tasks = Arrays.asList(json.fromJson(kvTaskClient.load("tasks"), Task[].class));
            for (Task task : tasks) {
                taskMap.put(task.getId(), task);
            }
            List<Epic> epics = Arrays.asList(json.fromJson(kvTaskClient.load("epics"), Epic[].class));
            for (Epic epic : epics) {
                epicMap.put(epic.getId(), epic);
            }
            List<Subtask> subtasks = Arrays.asList(json.fromJson(kvTaskClient.load("subtasks"), Subtask[].class));
            for (Subtask subtask : subtasks) {
                subtaskMap.put(subtask.getId(), subtask);
            }
            List<String> history = Arrays.asList(json.fromJson(kvTaskClient.load("history"), String[].class));
            for (String elem : history) {
                int id = Integer.parseInt(elem);
                if (taskMap.containsKey(id)) {
                    inMemoryHistoryManager.add(taskMap.get(id));
                } else if (epicMap.containsKey(id)) {
                    inMemoryHistoryManager.add(epicMap.get(id));
                } else if (subtaskMap.containsKey(id)) {
                    inMemoryHistoryManager.add(subtaskMap.get(id));
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Нет исходных данных. " + e);
        }
    }
}