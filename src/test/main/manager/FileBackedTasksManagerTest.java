package main.manager;

import main.models.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    String string = "src/tasks.txt";

    @BeforeEach
    void setupFileBackedTasksManager() {
        taskManager = new FileBackedTasksManager(string);
    }

    @Test
    void shouldReturnEmptyListTask() {
        taskManager.createTask(testTask);
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
        assertEquals(taskManager.getTasks().size(), 0, "Список задач не пустой.");
    }

    @Test
    void shouldReturnEmptyHistory1() {
        int taskId = taskManager.createTask(testTask);
        taskManager.getTask(taskId);
        taskManager.deleteTask(taskId);
        assertEquals(taskManager.getHistory().size(), 0, "История не пустая.");
    }

    @Test
    void shouldReturnEmptyHistory2() {
        assertEquals(taskManager.getHistory().size(), 0, "История не пустая.");
    }

    @Test
    public void shouldCorrectlySaveAndLoadState() {
        int testTaskId = taskManager.createTask(testTask);
        int testEpicId = taskManager.createEpic(testEpic);
        int testSubtask1Id = taskManager.createSubtask(testSubtask1);
        int testSubtask2Id = taskManager.createSubtask(testSubtask2);
        taskManager.getTask(testTaskId);
        taskManager.getEpic(testEpicId);
        taskManager.getSubtask(testSubtask1Id);
        taskManager.getSubtask(testSubtask2Id);
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(string);
        assertEquals(taskManager.getTask(testTaskId), taskManager2.getTask(testTaskId), "Задача восстановилась некорректно");
        assertEquals(taskManager.getEpic(testEpicId), taskManager2.getEpic(testEpicId), "Эпик восстановился некорректно");
        assertEquals(List.of(testSubtask1.getId(), testSubtask2.getId()), ((Epic) taskManager2.getEpic(testEpicId)).getSubtaskIdList(), "Подзадачи восстановились некорректно");
        assertEquals(taskManager.getHistory(), taskManager2.getHistory(), "История не совпадает.");
    }

    @Test
    void shouldReturnEmptyHistory() {
        taskManager.createTask(testTask);
        taskManager.createEpic(testEpic);
        taskManager.createSubtask(testSubtask1);
        taskManager.createSubtask(testSubtask2);
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(string);
        assertTrue(taskManager2.getHistory().isEmpty(), "Список истории не пустой.");
    }

    @Test
    void shouldReturnEmptyTasksList() throws IOException {
        Files.delete(Path.of(string));
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(string);
        assertTrue(taskManager2.getTasks().isEmpty(), "Список задач не пустой.");
    }

    @Test
    void shouldReturnEmptySubtasksList() {
        int testEpicId = taskManager.createEpic(testEpic);
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(string);
        assertTrue(((Epic) taskManager2.getEpic(testEpicId)).getSubtaskIdList().isEmpty(), "Список подзадач эпика не пустой.");
    }
}
