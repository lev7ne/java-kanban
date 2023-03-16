package manager;

import models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    static String source = "C:\\Users\\528\\Desktop\\YandexPraktikum\\java-kanban\\src\\tasks.txt";
    Path path;
    static HistoryManager historyManager = Managers.getDefaultInMemoryHistoryManager();

    /*
    Кучу всего пришлось сделать статическим, есть подозрение, что получилась ерунда, но тест проходит :)
     */

    public static void main(String[] args) {
        loadFromFile();
        System.out.println(taskMap);
        System.out.println(epicMap);
        System.out.println(subtaskMap);
        System.out.println();
        System.out.println(historyManager.getHistory());

    }

    public void save() {
        if (!Files.exists(Paths.get(source))) {
            try {
                this.path = Files.createFile(Paths.get(source));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка");
            }
        }
        try (Writer fileWriter = new FileWriter(source)) {
            fileWriter.append("id,type,name,status,description,epic" + "\n");
            for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
                fileWriter.write(pair.getValue().toString() + "\n");
            }
            for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
                fileWriter.write(pair.getValue().toString() + "\n");
            }
            for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
                fileWriter.write(pair.getValue().toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(inMemoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка");
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId()).append(",");
        }
        return sb.toString();
    }

    public static void loadFromFile() {
        if (Files.exists(Paths.get(source))) {
            try {
                String data = Files.readString(Path.of(source));
                String[] strings = data.split("\n");
                for (int i = 1; i < strings.length; i++) {
                    if (strings[i].equals("")) {

                        List<Integer> idsForHistory = historyFromString(strings[i + 1]);
                        for (Integer id : idsForHistory) {
                            if (taskMap.containsKey(id)) {
                                historyManager.add(taskMap.get(id));
                            }
                            if (epicMap.containsKey(id)) {
                                historyManager.add(epicMap.get(id));
                            }
                            if (subtaskMap.containsKey(id)) {
                                historyManager.add(subtaskMap.get(id));
                            }
                        }
                        return;
                    } else {
                        Task anyTask = fromString(strings[i]);
                        if (anyTask instanceof Subtask) {
                            subtaskMap.put(anyTask.getId(), (Subtask) anyTask);
                        } else if (anyTask instanceof Epic) {
                            epicMap.put(anyTask.getId(), (Epic) anyTask);
                        } else {
                            taskMap.put(anyTask.getId(), anyTask);
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка");
            }
        }
    }

    static Task fromString(String value) {
        String[] string = value.split(",");

        TaskType taskType = TaskType.valueOf(string[1]);
        switch (taskType) {
            case TASK:
                Integer idTask = Integer.parseInt(string[0]);
                String titleTask = string[2];
                Status statusTask = Status.valueOf(string[3]);
                String descriptionTask = string[4];
                return new Task(idTask, titleTask, statusTask, descriptionTask);
            case EPIC:
                Integer idEpic = Integer.parseInt(string[0]);
                String titleEpic = string[1];
                Status statusEpic = Status.valueOf(string[3]);
                String descriptionEpic = string[4];
                return new Epic(idEpic, titleEpic, statusEpic, descriptionEpic);
            case SUBTASK:
                Integer idSubtask = Integer.parseInt(string[0]);
                String titleSubtask = string[1];
                Status statusSubtask = Status.valueOf(string[3]);
                String descriptionSubtask = string[4];
                Integer idSubtaskEpic = Integer.parseInt(string[5]);
                return new Subtask(idSubtask, titleSubtask, statusSubtask, descriptionSubtask, idSubtaskEpic);
        }
        return null;
    }

    static List<Integer> historyFromString(String value) {
        String[] ids = value.split(",");
        List<Integer> idsList = new ArrayList<>();
        for (String id : ids) {
            idsList.add(Integer.parseInt(id));
        }
        return idsList;
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public Task getTask(Integer id) {
        super.getTask(id);
        save();
        return taskMap.get(id);
    }

    @Override
    public Task getEpic(Integer id) {
        super.getEpic(id);
        save();
        return epicMap.get(id);
    }

    @Override
    public Task getSubtask(Integer id) {
        super.getSubtask(id);
        save();
        return subtaskMap.get(id);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
