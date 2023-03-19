import manager.*;
import models.*;

import static models.Status.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        String string = "src/tasks.txt";
        TaskManager fileBackedTasksManager = new FileBackedTasksManager(string);

        Task task1 = new Task("название_таск", "описание_таск", NEW);
        fileBackedTasksManager.createTask(task1);

        Epic epic1 = new Epic("название_эпик", "описание_эпик", NEW);
        fileBackedTasksManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("название_сабтаск", "описание_сабтаск", DONE, epic1.getId());
        fileBackedTasksManager.createSubtask(subtask1);

        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getSubtask(3);
        fileBackedTasksManager.getEpic(2);
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(2);

        System.out.println(fileBackedTasksManager.getHistory());
    }
}