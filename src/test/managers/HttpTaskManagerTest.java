package managers;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import servers.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {
    private KVServer kvServer;
    public static final int PORT = 8078;
    private HttpTaskManager httpTaskManager;
    Task testTask1;
    Epic testEpic2;
    Subtask testSubtask3;
    Subtask testSubtask4;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.getDefaultHttpTaskManager(PORT);

        testTask1 = new Task(1, Status.NEW,"задача1","описание_задачи1",
                Instant.now(), Duration.ofMinutes(10)); // начинается сейчас, заканчивается +10 -> продолжительность 20:00 - 20:10
        testEpic2 = new Epic(2, Status.NEW,"эпик2","описание_эпика2",
                Instant.MIN, Duration.ZERO, new ArrayList<>()); // начинается сейчас +15, заканчивается +10 -> продолжительность 20:15 - 20:25
        testSubtask3 = new Subtask(3, Status.NEW, "подзадача3", "описание_подзадачи3",
                Instant.now().plus(Duration.ofMinutes(30)), Duration.ofMinutes(10), 2); // начинается сейчас +30, заканчивается +10 -> продолжительность 20:30 - 20:40
        testSubtask4 = new Subtask(4, Status.NEW,"подзадача4","описание_подзадачи4",
                Instant.now().plus(Duration.ofMinutes(45)), Duration.ofMinutes(10), 2); // начинается сейчас +45, заканчивается +10 -> продолжительность 20:45 - 20:55
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @DisplayName("Проверка работы по сохранению и восстановлению состояния менеджера")
    @Test
    public void shouldCorrectlySaveAndLoadCondition() {
        httpTaskManager.createTask(testTask1);
        httpTaskManager.createEpic(testEpic2);
        httpTaskManager.createSubtask(testSubtask3);
        httpTaskManager.createSubtask(testSubtask4);

        httpTaskManager.getTask(1);
        httpTaskManager.getEpic(2);
        httpTaskManager.getSubtask(3);

        httpTaskManager.save();

        HttpTaskManager httpTaskManager2 = new HttpTaskManager(PORT, true);

        assertEquals(httpTaskManager.taskMap, httpTaskManager2.taskMap, "Списки задач отличаются.");
        assertEquals(httpTaskManager.epicMap, httpTaskManager2.epicMap, "Списки эпиков отличаются.");
        assertEquals(httpTaskManager.subtaskMap, httpTaskManager2.subtaskMap, "Списки подзадач отличаются.");
        assertEquals(httpTaskManager.getHistory(),
                httpTaskManager2.getHistory(), "История отличается.");
        assertEquals(httpTaskManager.getId(), httpTaskManager2.getId(), "Счетчик восстановлен некорректно.");
    }
}

