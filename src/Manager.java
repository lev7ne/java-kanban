import java.util.ArrayList;
import java.util.List;

public class Manager {
    final String NEW = "NEW";
    final String IN_PROGRESS = "IN_PROGRESS";
    final String DONE = "DONE";
    Integer id = 0;
    List<Task> AllTasks = new ArrayList<>();

    void counterID() { // метод для увеличения счетчика, помогает присваивать всем Task уникальный идентификатор
        this.id++;
    }

    Task createTask(Task task) { // создание задачи (Task) с присвоением уникального идентификатора
        counterID();
        task.id = this.id;
        return task;
    }

    Epic createEpic(Epic epic) { // создание задачи (Epic) с присвоением уникального идентификатора
        counterID();
        epic.id = this.id;
        return epic;
    }

    Subtask createSubtask(Subtask subtask) { // создание задачи (Subtask) с присвоением уникального идентификатора
        counterID();
        subtask.id = id;



        return subtask;
    }

//    Epic updateEpic(Epic epic, Subtask subtask) {
//        epic.getSubtasks().add(subtask);
//    }

    void addAnyTaskInTasklist(Task task) { // добавление всех задач в общий список
        AllTasks.add(task);
    }

    void removeAllTask(Task task) { // очистка всего списка задач
        AllTasks.clear();
    }

    List<Task> gettingTasks(List<Task> AllTasks) { // получение из всего списка только задач (Task)
        List<Task> onlyTasks = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (!(someTask instanceof Subtask) && !(someTask instanceof Epic)) {
                System.out.println(someTask);
                onlyTasks.add(someTask);
            }
        }
        return onlyTasks;
    }

    List<Task> gettingEpics(List<Task> AllTasks) { // получение из всего списка только задач (Epic)
        List<Task> onlyTasks = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (someTask instanceof Subtask) {
                System.out.println(someTask);
                onlyTasks.add(someTask);
            }
        }
        return onlyTasks;
    }

    List<Task> gettingSubtasks(List<Task> AllTasks) { // получение из всего списка только задач (Subtask)
        List<Task> onlyTasks = new ArrayList<>();
        for (Task someTask : AllTasks) {
            if (someTask instanceof Epic) {
                System.out.println(someTask);
                onlyTasks.add(someTask);
            }
        }
        return onlyTasks;
    }

    Task returnById(Integer id, List<Task> AllTasks) {
        Task someTask = new Task();
        for (Task element : AllTasks) {
            if (element.id.equals(id)) {
                someTask = element;
            }
        }
        return someTask;
    }




}
