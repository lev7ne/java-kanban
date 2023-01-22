import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    final String NEW = "NEW";
    final String IN_PROGRESS = "IN_PROGRESS";
    final String DONE = "DONE";
    Integer id = 0;
    List<Task> AllTasks = new ArrayList<>();

    void counterID() { // метод для увеличения счетчика, помогает присваивать всем models.Task уникальный идентификатор
        this.id++;
    }

    Task createTask(Task task) { // создание задачи (models.Task) с присвоением уникального идентификатора
        counterID();
        task.setId(this.id);
        return task;
    }

    Epic createEpic(Epic epic) { // создание задачи (models.Epic) с присвоением уникального идентификатора
        counterID();
        epic.setId(this.id);
        return epic;
    }

    Subtask createSubtask(Subtask subtask, Epic epic) { // создание задачи (models.Subtask) с присвоением уникального идентификатора
        counterID();
        subtask.setId(id);
        subtask.epicID = epic.getId();
        epic.addNewSubtask(subtask);
        return subtask;
    }

    void addAnyTaskInTasklist(Task task) { // добавление задач в общий список
        AllTasks.add(task);
    }

    void removeAllTask(Task task) { // очистка всего списка задач
        AllTasks.clear();
    }

    List<Task> getterTasks(List<Task> AllTasks) { // получение из всего списка только задач (models.Task)
        List<Task> onlyTasks = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (!(someTask instanceof Subtask) && !(someTask instanceof Epic)) {
                System.out.println(someTask);
                onlyTasks.add(someTask);
            }
        }
        return onlyTasks;
    }

    List<Task> getterEpics(List<Task> AllTasks) { // получение из всего списка только задач (models.Epic)
        List<Task> onlyEpics = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (someTask instanceof Epic) {
                System.out.println(someTask);
                onlyEpics.add(someTask);
            }
        }
        return onlyEpics;
    }

    List<Task> getterSubtasks(List<Task> AllTasks) { // получение из всего списка только задач (models.Subtask)
        List<Task> onlySubtasks = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (someTask instanceof Subtask) {
                System.out.println(someTask);
                onlySubtasks.add(someTask);
            }
        }
        return onlySubtasks;
    }

    List<Task> removeTasks(List<Task> AllTasks) { // удаление из общего списка всех models.Task
        List<Task> besidesTasks = AllTasks;
        besidesTasks.removeIf(someTask -> !(someTask instanceof Subtask) && !(someTask instanceof Epic));
        return besidesTasks;
    }

    List<Task> removeEpics(List<Task> AllTasks) { // удаление из общего списка всех models.Epic
        List<Task> besidesEpicss = AllTasks;
        besidesEpicss.removeIf(someTask -> someTask instanceof Epic);
        return besidesEpicss;
    }

    List<Task> removeSubtasks(List<Task> AllTasks) { // удаление из общего списка всех models.Subtask
        List<Task> besidesSubtasks = AllTasks;
        besidesSubtasks.removeIf(someTask -> someTask instanceof Subtask);
        return besidesSubtasks;
    }

    Task returnById(Integer id, List<Task> AllTasks) {
        Task someTask = new Task();
        for (Task element : AllTasks) {
            if (element.getId().equals(id)) {
                someTask = element;
            }
        }
        return someTask;
    }

    void updateTask (Integer id, Task task) {
        for (Task element : AllTasks) {
            if (!(element instanceof Subtask) && !(element instanceof Epic)) {
                if (element.getId().equals(id)) {
                    element.setId(id);
                    element.setTitle(task.getTitle());
                    element.setDescription(task.getDescription());
                    element.setStatus(task.getStatus());
                }
            }
        }
    }

    void updateEpic (Integer id, Epic epic) {
        for (Task element : AllTasks) {
            if (element instanceof Epic) {
                if (element.getId().equals(id)) {
                    element.setId(id);
                    element.setTitle(epic.getTitle());
                    element.setDescription(epic.getDescription());
                }
            }
        }
    }
    
}




