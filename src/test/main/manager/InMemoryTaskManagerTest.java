package main.manager;

import main.models.Status;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setupInMemoryTaskManager() {
        taskManager = (InMemoryTaskManager) Managers.getDefaultInMemoryTaskManager();
    }

    @Test
    void comparatorShouldWorkCorrectly() { // отдельный тест работы компаратора, после первой итерации проверки ТЗ

//        По условиям ТЗ задачи с отсутствующей датой должны относиться в конец.
//        Т.е. если из двух задач у какой-то дата не null, то она "больше",
//        а если у обеих задач дата не задана, то их нужно сравнивать по какому-то другому признаку, например по идентификатору

        Task task1_WithoutStartTime = new Task(1, Status.NEW, "", "позиция-3",
                null, Duration.ofMinutes(10));
        Task task2_WithoutStartTime = new Task(2, Status.NEW,"","позиция-4",
                null, Duration.ofMinutes(10));
        Task task3 = new Task(3, Status.NEW,"","позиция-1",
                Instant.now(), Duration.ofMinutes(10));
        Task task4 = new Task(4, Status.NEW,"","позиция-2",
                Instant.now().plus(Duration.ofMinutes(15)), Duration.ofMinutes(10));

        taskManager.createTask(task3);
        taskManager.createTask(task1_WithoutStartTime);
        taskManager.createTask(task2_WithoutStartTime);
        taskManager.createTask(task4);

        List<Task> resultList = List.of(task3, task4, task1_WithoutStartTime, task2_WithoutStartTime);
        System.out.println(resultList);

        List<Task> prioritizedList = new ArrayList<>(taskManager.getPrioritizedTasks());
        System.out.println(taskManager.getPrioritizedTasks());

        assertEquals(prioritizedList, prioritizedList, "Списки не совпадают.");
    }
}