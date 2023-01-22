import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        Manager manager = new Manager();
        System.out.println("--созданю два обычных таска из конструктора без присвоения ID--");
        Task task1 = new Task("таск", "описание", manager.NEW);
        Task task2 = new Task("таск", "описание", manager.IN_PROGRESS);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--двум обычным таскам присваиваю ID--");
        Task newTask1 = manager.createTask(task1);
        Task newTask2 = manager.createTask(task2);
        System.out.println(newTask1);
        System.out.println(newTask2);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--создаю два эпика из конструктора без присвоения ID--");
        Epic epic1 = new Epic("эпик", "описание");
        Epic epic2 = new Epic("эпик", "описание");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--двум эпикам присвоен ID--");
        Epic newEpic1 = manager.createEpic(epic1);
        Epic newEpic2 = manager.createEpic(epic2);
        System.out.println(newEpic1);
        System.out.println(newEpic2);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--созданы три новых сабтаска без ID--");
        Subtask subtask1 = new Subtask("сабтаск", "описание", manager.NEW);
        Subtask subtask2 = new Subtask("сабтаск", "описание", manager.DONE);
        Subtask subtask3 = new Subtask("сабтаск", "описание", manager.DONE);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--трем сабтаскам присвоен ID и они добавлены в соответствующие эпики, у эпиков обновлен статус--");
        Subtask newSubtask1 = manager.createSubtask(subtask1, newEpic1);
        Subtask newSubtask2 = manager.createSubtask(subtask2, newEpic1);
        Subtask newSubtask3 = manager.createSubtask(subtask3, newEpic2);
        System.out.println(newSubtask1);
        System.out.println(newSubtask2);
        System.out.println(newSubtask3);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--вывод новых эпиков--");
        System.out.println(newEpic1);
        System.out.println(newEpic2);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--добавляю все задачи в общий список--");
        manager.AllTasks.add(newTask1);
        manager.AllTasks.add(newTask2);
        manager.AllTasks.add(newEpic1);
        manager.AllTasks.add(newEpic2);
        manager.AllTasks.add(newSubtask1);
        manager.AllTasks.add(newSubtask2);
        manager.AllTasks.add(newSubtask3);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--вывожу только Tasks--");
        List<Task> onlyTasks = manager.getterTasks(manager.AllTasks);
        System.out.println(onlyTasks);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--вывожу только Epics--");
        List<Task> onlyEpics = manager.getterEpics(manager.AllTasks);
        System.out.println(onlyEpics);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--вывожу только Subtasks--");
        List<Task> onlySubtasks = manager.getterSubtasks(manager.AllTasks);
        System.out.println(onlySubtasks);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--возвращаю какую-то задачу по ID--");
        Task testReturnTaskId = manager.returnById(2, manager.AllTasks);
        System.out.println(testReturnTaskId);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--удаляю из общего списка все Tasks--");
        List<Task> AllTasksAfterRemoveTasks = manager.removeTasks(manager.AllTasks);
        System.out.println(AllTasksAfterRemoveTasks);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--удаляю из общего списка все Epics--");
        List<Task> AllTasksAfterRemoveEpics = manager.removeEpics(manager.AllTasks);
        System.out.println(AllTasksAfterRemoveEpics);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--удаляю из общего списка все Subtasks--");
        List<Task> AllTasksAfterRemoveSubtasks = manager.removeSubtasks(manager.AllTasks);
        System.out.println(AllTasksAfterRemoveSubtasks);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--добавляю все задачи в общий список--");
        manager.AllTasks.add(newTask1);
        manager.AllTasks.add(newTask2);
        manager.AllTasks.add(newEpic1);
        manager.AllTasks.add(newEpic2);
        manager.AllTasks.add(newSubtask1);
        manager.AllTasks.add(newSubtask2);
        manager.AllTasks.add(newSubtask3);
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--проверка работы метода с обновлением Task--");
        Task taskForUpdate = new Task("НОВОЕ ИМЯ ТАСКА", "НОВОЕ ОПИСАНИЕ ТАСКА", manager.NEW);
        manager.updateTask(2, taskForUpdate);
        System.out.println(manager.returnById(2, manager.AllTasks));
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--проверка работы метода с обновлением Epic--");
        Epic epicForUpdate = new Epic("НОВОЕ ИМЯ ЭПИКА", "НОВОЕ ОПИСАНИЕ ЭПИКА");
        manager.updateEpic(3, epicForUpdate);
        System.out.println(manager.returnById(3, manager.AllTasks));
        System.out.println("---------------------------------------------------------------"+ "\n");

        System.out.println("--получение списка Subtask в Эпик--");


        System.out.println("---------------------------------------------------------------"+ "\n");

    }
}
