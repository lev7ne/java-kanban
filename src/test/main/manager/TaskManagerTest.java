package main.manager;

import main.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

import static main.models.Status.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    Task testTask;
    Epic testEpic;
    Subtask testSubtask1;
    Subtask testSubtask2;

    @BeforeEach
    void creator() {
        testTask = new Task(1, Status.NEW,"задача1","описание_задачи1",
                Instant.now(), Duration.ofMinutes(10)); // начинается сейчас, заканчивается +10 -> продолжительность 20:00 - 20:10
        testEpic = new Epic(2, Status.NEW,"эпик2","описание_эпика2",
                Instant.MIN, Duration.ZERO, new ArrayList<>()); // начинается сейчас +15, заканчивается +10 -> продолжительность 20:15 - 20:25
        testSubtask1 = new Subtask(3, Status.NEW, "подзадача3", "описание_подзадачи3",
                Instant.now().plus(Duration.ofMinutes(30)), Duration.ofMinutes(10), 2); // начинается сейчас +30, заканчивается +10 -> продолжительность 20:30 - 20:40
        testSubtask2 = new Subtask(4, Status.NEW,"подзадача4","описание_подзадачи4",
                Instant.now().plus(Duration.ofMinutes(45)), Duration.ofMinutes(10), 2); // начинается сейчас +45, заканчивается +10 -> продолжительность 20:45 - 20:55
    }

    @Test
    void shouldCreateTask() { // тестирование метода createTask()
        int taskId = taskManager.createTask(testTask);
        Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не пустая."); // если не в 16 не получилось достать таску по id, выводится сообщение
        assertEquals(testTask, savedTask, "Задачи не совпадают."); // если таска, которую достали по id не равна исходной, выводится сообщение

        List<Task> tasks = taskManager.getTasks(); // создаем новый List и кладем в него результат .getTasks()
        assertNotNull(tasks, "Задачи на возвращаются."); // проверяем что новый список не пустой
        assertEquals(1, tasks.size(), "Неверное количество задач."); // проверяем размер списка
        assertEquals(testTask, tasks.get(0), "Задачи не совпадают."); // проверяем что элемент под индексом 0 совпадает с исходной task1
    }

    @Test
    void shouldCreateEpicInEpicMap() { // тестирование метода createEpic()
        int epicId = taskManager.createEpic(testEpic);
        Epic savedEpic = (Epic) taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(testEpic, savedEpic, "Задачи не совпадают.");

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(testEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateSubtaskInSubtaskMap() { // тестирование метода createSubtask()
        int subtaskId = taskManager.createSubtask(testSubtask1);
        Subtask savedSubtask = (Subtask) taskManager.getSubtask(subtaskId);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(testSubtask1, savedSubtask, "Задачи не совпадают.");

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(testSubtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldDeleteTaskById() {
        int taskId = taskManager.createTask(testTask);
        taskManager.deleteTask(taskId);
        assertEquals(taskManager.getTasks().size(), 0, "Удаление не происходит.");
    }

    @Test
    void defaultEpicStatusMustBeNewWithoutSubtask() { // 1. a. Пустой список подзадач.
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setStatus(IN_PROGRESS);
        testSubtask1.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.deleteSubtask(testSubtask1.getId());
        assertEquals(testEpic.getStatus(), NEW, "Некорректный статус");
    }

    @Test
    void shouldUpdateEpicStatusToNEW() { // 1. b. Все подзадачи со статусом NEW.
        taskManager.createEpic(testEpic);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertEquals(testEpic.getStatus(), NEW, "Корректный статус не присвоен.");
    }

    @Test
    void shouldUpdateEpicStatusToDONE() { // 1. c. Все подзадачи со статусом DONE.
        int epicId = taskManager.createEpic(testEpic); // id
        testSubtask1.setStatus(DONE);
        testSubtask2.setStatus(DONE);
        testSubtask1.setEpicId(epicId);
        testSubtask2.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertEquals(testEpic.getStatus(), DONE, "Корректный статус не присвоен.");
    }

    @Test
    void shouldUpdateEpicStatusToIN_PROGRESSifSubtaskHasStatusNEWandDONE() { // 1. d. Подзадачи со статусами NEW и DONE
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setStatus(DONE);
        testSubtask2.setStatus(NEW);
        testSubtask1.setEpicId(epicId);
        testSubtask2.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertEquals(testEpic.getStatus(), IN_PROGRESS, "Корректный статус не присвоен.");
    }

    @Test
    void shouldUpdateEpicStatusToIN_PROGRESS() { // 1. e. Подзадачи со статусом IN_PROGRESS.
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setStatus(IN_PROGRESS);
        testSubtask2.setStatus(IN_PROGRESS);
        testSubtask1.setEpicId(epicId);
        testSubtask2.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertEquals(testEpic.getStatus(), IN_PROGRESS, "Корректный статус не присвоен.");
    }

    @Test
    void shouldReturnListTasks() { // тестирование метода getTasks()
        taskManager.createTask(testTask);
        assertNotNull(taskManager.getTasks(), "List<Task> пустой.");
        assertEquals(taskManager.getTasks().size(), 1, "Размер тестового List<Task> не совпадает по размеру с тестовой величиной 2.");
        assertEquals(List.of(testTask), taskManager.getTasks(), "Тестовый List<Task> не соответствует, созданному через getTasks() List<Task>.");
    }

    @Test
    void shouldReturnListEpics() { // тестирование метода getEpics()
        taskManager.createEpic(testEpic);
        assertNotNull(taskManager.getEpics(), "List<Epic> пустой.");
        assertEquals(taskManager.getEpics().size(), 1, "Размер тестового List<Epic> не совпадает по размеру с тестовой величиной 2.");
        assertEquals(List.of(testEpic), taskManager.getEpics(), "Тестовый List<Epic> не соответствует, созданному через getEpics() List<Epic>.");
    }

    @Test
    void shouldReturnListSubtasks() { // тестирование метода getSubtask()
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertNotNull(taskManager.getSubtasks(), "List<Subtask> пустой.");
        assertEquals(taskManager.getSubtasks().size(), 2, "Размер тестового List<Subtask> не совпадает по размеру с тестовой величиной 2.");
        assertEquals(List.of(testSubtask1, testSubtask2), taskManager.getSubtasks(), "Тестовый List<Subtask> не соответствует, созданному через getSubtasks() List<Subtask>.");
    }

    @Test
    void shouldReturnListSubtasksFromEpic() { // тестирование метода getEpicSubtasks()
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setEpicId(epicId);
        testSubtask2.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        assertNotNull(taskManager.getEpics().get(0).getSubtaskIdList(), "Список пустой.");
        assertEquals(taskManager.getEpics().get(0).getSubtaskIdList(), List.of(testSubtask1.getId(), testSubtask2.getId()), "В epicSubtasksIdList<Subtask> лежат не те Subtasks");
    }

    @Test
    void shouldUpdateTask() {
        taskManager.createTask(testTask);
        testTask.setName("новое_имя");
        testTask.setDescription("новое_описание");
        taskManager.updateTask(testTask);
        assertEquals(taskManager.getTask(testTask.getId()).getName(), "новое_имя", "Название не изменилось.");
        assertEquals(taskManager.getTask(testTask.getId()).getDescription(), "новое_описание", "Описание не изменилось.");
    }

    @Test
    void shouldNotUpdateTaskIfIdIncorrect() {
        taskManager.updateTask(testTask);
        assertEquals(testTask.getName(), "задача1", "Имя обновлено.");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.createTask(testEpic);
        testEpic.setName("новое_имя");
        testEpic.setDescription("новое_описание");
        taskManager.updateTask(testEpic);
        assertEquals(taskManager.getTask(testEpic.getId()).getName(), "новое_имя", "Название не изменилось.");
        assertEquals(taskManager.getTask(testEpic.getId()).getDescription(), "новое_описание", "Описание не изменилось.");
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.createTask(testSubtask1);
        testSubtask1.setName("новое_имя");
        testSubtask1.setDescription("новое_описание");
        taskManager.updateTask(testSubtask1);
        assertEquals(taskManager.getTask(testSubtask1.getId()).getName(), "новое_имя", "Название не изменилось.");
        assertEquals(taskManager.getTask(testSubtask1.getId()).getDescription(), "новое_описание", "Описание не изменилось.");
    }

    @Test
    void shouldUpdateEpicDurationAndEpicStartTimeAndEpicEndTime() {
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setEpicId(epicId);
        testSubtask2.setEpicId(epicId);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        Duration resultDuration = testSubtask1.getDuration().plus(testSubtask2.getDuration());
        Instant resultStartTime = testSubtask1.getStartTime().isBefore(testSubtask2.getStartTime()) ? testSubtask1.getStartTime() : testSubtask2.getStartTime();
        Instant resultEndTime = testSubtask1.getEndTime().isAfter(testSubtask2.getEndTime()) ? testSubtask1.getEndTime() : testSubtask2.getEndTime();
        assertEquals(testEpic.getDuration(), resultDuration, "Epic.duration считается некорректно.");
        assertEquals(testEpic.getStartTime(), resultStartTime, "Epic.startTime считается некорректно.");
        assertEquals(testEpic.getEndTime(), resultEndTime, "Epic.endTime считается некорректно.");
    }

    @Test
    void shouldDeleteTask() {
        int taskId = taskManager.createTask(testTask);
        taskManager.deleteTask(taskId);
        assertEquals(taskManager.getTasks().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldDeleteEpic() {
        int epicId = taskManager.createEpic(testEpic);
        taskManager.deleteEpic(epicId);
        assertEquals(taskManager.getEpics().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldDeleteSubtask() {
        int subtaskId = taskManager.createSubtask(testSubtask1);
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setEpicId(epicId);
        taskManager.deleteSubtask(subtaskId);
        assertEquals(taskManager.getSubtasks().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldDeleteTasks() {
        taskManager.createTask(testTask);
        taskManager.deleteTasks();
        assertEquals(taskManager.getTasks().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldDeleteEpics() {
        taskManager.createEpic(testEpic);
        taskManager.deleteEpics();
        assertEquals(taskManager.getEpics().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldDeleteSubtasks() {
        taskManager.createSubtask(testSubtask1);
        int epicId = taskManager.createEpic(testEpic);
        testSubtask1.setEpicId(epicId);
        taskManager.deleteSubtasks();
        assertEquals(taskManager.getSubtasks().size(), 0, "Задача не удалилась.");
    }

    @Test
    void shouldReturnHistory() {
        int taskId = taskManager.createTask(testTask);
        int epicId = taskManager.createEpic(testEpic);
        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getEpic(epicId);
        assertEquals(taskManager.getHistory(), List.of(testTask, testEpic), "История выводится некорректно.");
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        taskManager.createTask(testTask);
        taskManager.createTask(testSubtask1);
        taskManager.createTask(testSubtask2);
        List<Task> list = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertEquals(List.of(testTask, testSubtask1, testSubtask2), list, "Порядок не соответствует.");
    }

    @Test
    void shouldThrowManagerValidateTaskException() {
        Task anyTask2 = new Task(2, Status.NEW,"задача2","описание_задачи2",
                Instant.now().plus(Duration.ofMinutes(5)), Duration.ofMinutes(10));
        taskManager.createTask(testTask);
        taskManager.createEpic(testEpic);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);

        assertThrows(ManagerValidateTaskException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        taskManager.createTask(anyTask2);
                    }
                });
    }
}