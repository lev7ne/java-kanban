package main.manager;

import main.models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    final Path path;

    public FileBackedTasksManager(String string) {
        this.path = Paths.get(string);
    }

    private void save() {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка");
            }
        }
        try (Writer fileWriter = new FileWriter("src/tasks.txt")) {
            fileWriter.append("id;type;status;name;description;startTime;duration;epicId/SubtaskIdList" + "\n");
            for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
                fileWriter.write(FileBackedTasksManager.toCsvFormatConverter(pair.getValue()) + "\n");
            }
            for (Map.Entry<Integer, Epic> pair : epicMap.entrySet()) {
                fileWriter.write(FileBackedTasksManager.toCsvFormatConverter(pair.getValue()) + "\n");
            }
            for (Map.Entry<Integer, Subtask> pair : subtaskMap.entrySet()) {
                fileWriter.write(FileBackedTasksManager.toCsvFormatConverter(pair.getValue()) + "\n");
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
            sb.append(task.getId()).append(";");
        }
        return sb.toString();
    }

    public static FileBackedTasksManager loadFromFile(String string) {
        FileBackedTasksManager mgr = new FileBackedTasksManager(string);
        if (Files.exists(mgr.path)) {
            try {
                String data = Files.readString(mgr.path);
                String[] strings = data.split("\n");
                for (int i = 1; i < strings.length; i++) {
                    if (strings[i].equals("")) {
                        List<Integer> idsForHistory = historyFromString(strings[i + 1]);
                        for (Integer id : idsForHistory) {
                            if (mgr.taskMap.containsKey(id)) {
                                mgr.inMemoryHistoryManager.add(mgr.taskMap.get(id));
                            }
                            if (mgr.epicMap.containsKey(id)) {
                                mgr.inMemoryHistoryManager.add(mgr.epicMap.get(id));
                            }
                            if (mgr.subtaskMap.containsKey(id)) {
                                mgr.inMemoryHistoryManager.add(mgr.subtaskMap.get(id));
                            }
                        }
                        break;
                    } else {
                        Task anyTask = fromString(strings[i]);
                        mgr.setId(Math.max(mgr.getId(), anyTask.getId()));
                        if (anyTask instanceof Subtask) {
                            Subtask anySubtask = (Subtask) anyTask;
                            Epic anyEpic = mgr.epicMap.get(anySubtask.getEpicId());
                            if (anyEpic != null) {
                                anyEpic.getSubtaskIdList().add(anySubtask.getId());
                                mgr.subtaskMap.put(anyTask.getId(), (Subtask) anyTask);
                            }
                        } else if (anyTask instanceof Epic) {
                            mgr.epicMap.put(anyTask.getId(), (Epic) anyTask);
                        } else {
                            mgr.taskMap.put(anyTask.getId(), anyTask);
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerLoadException("Ошибка");
            }
        }
        return mgr;
    }

    static List<Integer> historyFromString(String value) {
        String[] ids = value.split(";");
        List<Integer> idsList = new ArrayList<>();
        for (String id : ids) {
            idsList.add(Integer.parseInt(id));
        }
        return idsList;
    }

    static Task fromString(String value) {
        String[] string = value.split(";");
        TaskType taskType = TaskType.valueOf(string[1]);
        switch (taskType) {
            case TASK:
                Integer idTask = Integer.parseInt(string[0]);
                Status statusTask = Status.valueOf(string[2]);
                String nameTask = string[3];
                String descriptionTask = string[4];
                Instant startTimeTask = Instant.parse(string[5]);
                Duration durationTask = Duration.parse(string[6]);
                return new Task(idTask, statusTask, nameTask, descriptionTask, startTimeTask, durationTask);
            case EPIC:
                Integer idEpic = Integer.parseInt(string[0]);
                Status statusEpic = Status.valueOf(string[2]);
                String nameEpic = string[3];
                String descriptionEpic = string[4];
                Instant startTimeEpic = Instant.parse(string[5]);
                Duration durationEpic = Duration.parse(string[6]);
                List<String> source = List.of(string[7].split(","));
                ArrayList<Integer> subtaskIdsEpic = new ArrayList<>();
                for (int i = 1; i < source.size() - 2; i++) {
                    subtaskIdsEpic.add(Integer.parseInt(source.get(i)));
                }
                return new Epic(idEpic, statusEpic, nameEpic, descriptionEpic, startTimeEpic, durationEpic, subtaskIdsEpic);
            case SUBTASK:
                Integer idSubtask = Integer.parseInt(string[0]);
                Status statusSubtask = Status.valueOf(string[2]);
                String nameSubtask = string[3];
                String descriptionSubtask = string[4];
                Instant startTimeSubtask = Instant.parse(string[5]);
                Duration durationSubtask = Duration.parse(string[6]);
                Integer epicId = Integer.parseInt(string[7]);
                return new Subtask(idSubtask, statusSubtask, nameSubtask, descriptionSubtask, startTimeSubtask, durationSubtask, epicId);
        }
        return null;
    }

    private static String toCsvFormatConverter(Task task) {
        if (task instanceof Subtask) {
            return task.getId() + ";"
                    + TaskType.SUBTASK + ";"
                    + task.getStatus() + ";"
                    + task.getName() + ";"
                    + task.getDescription() + ";"
                    + task.getStartTime() + ";"
                    + task.getDuration() + ";"
                    + ((Subtask) task).getEpicId();

        } else if (task instanceof Epic) {

            return task.getId() + ";"
                    + TaskType.EPIC + ";"
                    + task.getStatus() + ";"
                    + task.getName() + ";"
                    + task.getDescription() + ";"
                    + task.getStartTime() + ";"
                    + task.getDuration() + ";"
                    + ((Epic) task).getSubtaskIdList();
        } else {
            return task.getId() + ";"
                    + TaskType.TASK + ";"
                    + task.getStatus() + ";"
                    + task.getName() + ";"
                    + task.getDescription() + ";"
                    + task.getStartTime() + ";"
                    + task.getDuration();
        }
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

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
}