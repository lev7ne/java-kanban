import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");



        Manager manager = new Manager();

        Task task1 = new Task("таск", "описание", manager.NEW);
        Task task2 = new Task("таск", "описание", manager.IN_PROGRESS);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println("--создано два обычных таска из конструктора без присвоения ID--");

        Task newTask1 = manager.createTask(task1);
        Task newTask2 = manager.createTask(task2);
        System.out.println(newTask1);
        System.out.println(newTask2);
        System.out.println("--двум обычным таскам присвоен ID--");

        Epic epic1 = new Epic("эпик", "описание");
        Epic epic2 = new Epic("эпик", "описание");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println("--создано два эпика из конструктора без присвоения ID--");

        Epic newEpic1 = manager.createEpic(epic1);
        Epic newEpic2 = manager.createEpic(epic2);
        System.out.println(newEpic1);
        System.out.println(newEpic2);
        System.out.println("--двум эпикам присвоен ID--");

        Subtask subtask1 = new Subtask("сабтаск", "описание", manager.NEW, newEpic1);
        Subtask subtask2 = new Subtask("сабтаск", "описание", manager.DONE, newEpic1);
        Subtask subtask3 = new Subtask("сабтаск", "описание", manager.DONE, newEpic2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);
        System.out.println("--двум эпикам присвоен ID--");

        Subtask newSubtask1 = manager.createSubtask(subtask1);
        Subtask newSubtask2 = manager.createSubtask(subtask2);
        System.out.println(newSubtask1);
        System.out.println(newSubtask2);

        List<Task> allTasks = new ArrayList<>();
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(epic1);
        allTasks.add(epic2);
        allTasks.add(subtask1);
        allTasks.add(subtask2);

        System.out.println("----------------------------------");
        System.out.println("Только Tasks:");

        List<Task> onlyTasks = new ArrayList<>();
        manager.gettingTasks(allTasks);

        System.out.println("----------------------------------");
        System.out.println("Только Subtasks:");

        List<Task> onlyEpics = new ArrayList<>();
        manager.gettingEpics(allTasks);

        System.out.println("----------------------------------");
        System.out.println("Только Epics:");

        List<Task> onlySubtasks = new ArrayList<>();
        manager.gettingSubtasks(allTasks);

        System.out.println("----------------------------------");
        System.out.println("Возвращаем какую-то Task по ID:");
        Task taskId3 = manager.returnById(2, allTasks);
        System.out.println(taskId3);
    }

}
