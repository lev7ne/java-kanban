package main.manager;

import main.models.Epic;
import main.models.Status;
import main.models.Subtask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();

    Task testTask;
    Epic testEpic;
    Subtask testSubtask1;
    Subtask testSubtask2;

    @BeforeEach
    void creator() {
        testTask = new Task(1, Status.NEW,"задача1","описание_задачи1",
                Instant.now(), Duration.ofMinutes(10)); // начинается сейчас, заканчивается +10 -> продолжительность 20:00 - 20:10
        testEpic = new Epic(2, Status.NEW,"эпик2","описание_эпика2",
                Instant.now().plus(Duration.ofMinutes(15)), Duration.ofMinutes(10), new ArrayList<>()); // начинается сейчас +15, заканчивается +10 -> продолжительность 20:15 - 20:25
        testSubtask1 = new Subtask(3, Status.NEW, "подзадача3", "описание_подзадачи3",
                Instant.now().plus(Duration.ofMinutes(30)), Duration.ofMinutes(10), 2); // начинается сейчас +30, заканчивается +10 -> продолжительность 20:30 - 20:40
        testSubtask2 = new Subtask(4, Status.NEW,"подзадача4","описание_подзадачи4",
                Instant.now().plus(Duration.ofMinutes(45)), Duration.ofMinutes(10), 2); // начинается сейчас +45, заканчивается +10 -> продолжительность 20:45 - 20:55
    }

    @Test
    void shouldReturnOnlyOneUniqueRecord() { // 3. b. Дублирование.
        historyManager.add(testTask);
        historyManager.add(testTask);
        historyManager.add(testTask);
        assertEquals(1, historyManager.getHistory().size(), "Произошло дублирование.");
    }

    @Test
    void shouldRemoveFromIfIncorrectId() { // передать неверный id
        historyManager.add(testTask);
        historyManager.add(testEpic);
        historyManager.add(testSubtask1);
        historyManager.add(testSubtask2);
        historyManager.remove(56);
        assertEquals(historyManager.getHistory().size(), 4, "Что-то изменилось.");
    }

    @Test // 3. с. Удаление из истории: начало, середина, конец.
    void shouldRemoveFromBeginning() {
        historyManager.add(testTask);
        historyManager.add(testEpic);
        historyManager.add(testSubtask1);
        historyManager.add(testSubtask2);
        historyManager.remove(1);
        assertEquals(historyManager.getHistory().get(0), testEpic);
        assertEquals(historyManager.getHistory().get(1), testSubtask1);
        assertEquals(historyManager.getHistory().get(2), testSubtask2);
    }

    @Test // 3. с. Удаление из истории: начало, середина, конец.
    void shouldRemoveFromEmptyHistory() {
        historyManager.add(testTask);
        historyManager.add(testEpic);
        historyManager.add(testSubtask1);
        historyManager.add(testSubtask2);
        historyManager.remove(3);
        assertEquals(historyManager.getHistory().get(0), testTask);
        assertEquals(historyManager.getHistory().get(1), testEpic);
        assertEquals(historyManager.getHistory().get(2), testSubtask2);
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(0, historyManager.getHistory().size(), "Не возвращается пустая история.");
    }

    @Test
    public void shouldRemoveTaskInMiddleFromHistory() {
        historyManager.add(testTask);
        historyManager.add(testEpic);
        historyManager.add(testSubtask1);
        historyManager.remove(2);
        assertEquals(List.of(testTask, testSubtask1), historyManager.getHistory(),"Задача не удалилась из середины.");
    }
}
