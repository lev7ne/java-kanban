import manager.*;
import models.*;

import static models.Status.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager inMemoryTaskManager = new InMemoryTaskManager();
        TaskManager fileBackedTasksManager = new FileBackedTasksManager();



        Task task1 = new Task("название_таск", "описание_таск", NEW);
        inMemoryTaskManager.createTask(task1);

        Epic epic1 = new Epic("название_эпик", "описание_эпик", NEW);
        inMemoryTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("название_сабтаск", "описание_сабтаск", DONE, epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);


        System.out.println(inMemoryTaskManager.getHistory());

    }
}