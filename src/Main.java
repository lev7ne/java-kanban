import models.Epic;
import models.Subtask;
import models.Task;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        Manager manager = new Manager();

        // создание объектов Task и добавление в MapTasks
        Task task1 = new Task("task1", "task_description1", Manager.NEW);
        Task task2 = new Task("task2", "task_description2", Manager.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);
        
        // создание объектов Epic и Subtask и добавление их в соответствующие MapEpic, MapSubtask
        Epic epic1 = new Epic("epic1", "epic_description1", Manager.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask_description1", Manager.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "subtask_description2", Manager.IN_PROGRESS, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2", "description2", Manager.NEW);
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("subtask3", "subtask_description3", Manager.IN_PROGRESS, epic2.getId());
        manager.createSubtask(subtask3);

        // вывод всех мап
        System.out.println(manager.taskMap);
        System.out.println(manager.epicMap);
        System.out.println(manager.subtaskMap);
    }
    
}