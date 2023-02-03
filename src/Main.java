import manager.*;
import models.*;

import static models.Status.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("task1", "task_description1", NEW);
        Task task2 = new Task("task2", "task_description2", IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("epic1", "epic_description1", NEW);
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "subtask_description1", DONE, epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "subtask_description2", IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("epic2", "description2", NEW);
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("subtask3", "subtask_description3", IN_PROGRESS, epic2.getId());
        taskManager.createSubtask(subtask3);

        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);

        System.out.println(taskManager.getHistory());
    }
}