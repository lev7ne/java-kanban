package servers;

import com.google.gson.Gson;
import managers.HttpTaskManager;
import managers.Managers;
import managers.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private HttpClient client;
    public static final int PORT = 8078;
    private TaskManager httpTaskManager;
    private Gson gson;
    Task testTask1;
    Epic testEpic2;
    Subtask testSubtask3;
    Subtask testSubtask4;
    Task testTask5;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.getDefaultHttpTaskManager(PORT);

        httpTaskServer = new HttpTaskServer((HttpTaskManager) httpTaskManager);
        httpTaskServer.start();

        client = HttpClient.newHttpClient();
        gson = Managers.getGson();

        testTask1 = new Task(1, Status.NEW, "задача1", "описание_задачи1",
                Instant.now(), Duration.ofMinutes(10)); // начинается сейчас, заканчивается +10 -> продолжительность 20:00 - 20:10
        testEpic2 = new Epic(2, Status.NEW, "эпик2", "описание_эпика2",
                Instant.MIN, Duration.ZERO, new ArrayList<>()); // начинается сейчас +15, заканчивается +10 -> продолжительность 20:15 - 20:25
        testSubtask3 = new Subtask(3, Status.NEW, "подзадача3", "описание_подзадачи3",
                Instant.now().plus(Duration.ofMinutes(30)), Duration.ofMinutes(10), 2); // начинается сейчас +30, заканчивается +10 -> продолжительность 20:30 - 20:40
        testSubtask4 = new Subtask(4, Status.NEW, "подзадача4", "описание_подзадачи4",
                Instant.now().plus(Duration.ofMinutes(45)), Duration.ofMinutes(10), 2); // начинается сейчас +45, заканчивается +10 -> продолжительность 20:45 - 20:55
        testTask5 = new Task(5, Status.NEW, "задача5", "описание_задачи5",
                Instant.now().plus(Duration.ofMinutes(60)), Duration.ofMinutes(10));
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @DisplayName("Проверка обработки запроса POST и GET с Task")
    @Test
    void shouldWorkPostAndGetWithTask() {
        URI url1 = URI.create("http://localhost:8080/tasks/task");
        String json1 = gson.toJson(testTask1);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(body1)
                .build();
        HttpResponse<String> response1 = getResponse(request1);

        assertEquals("Создана задача c id=" + testTask1.getId() + ".", response1.body());

        URI url2 = URI.create("http://localhost:8080/tasks/task?id=" + testTask1.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = getResponse(request2);
        assertEquals(response2.statusCode(), 200);

        Task task = gson.fromJson(response2.body(), Task.class);
        assertEquals(task, testTask1, "Задачи отличаются.");
    }

    @DisplayName("Проверка обработки запроса POST и GET с Epic")
    @Test
    void shouldWorkPostAndGetWithEpic() {
        httpTaskManager.createTask(testTask1);
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        String json1 = gson.toJson(testEpic2);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(body1)
                .build();
        HttpResponse<String> response = getResponse(request1);
        assertEquals("Создан эпик c id=" + testEpic2.getId() + ".", response.body());

        URI url2 = URI.create("http://localhost:8080/tasks/epic?id=" + testEpic2.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response2 = getResponse(request2);

        Epic epic = gson.fromJson(response2.body(), Epic.class);
        assertEquals(epic, testEpic2, "Эпики отличаются.");
    }

    @Test
    void shouldReturnCorrectHistory() {
        int testTaskId1 = httpTaskManager.createTask(testTask1);
        int testTaskId2 = httpTaskManager.createTask(testTask5);

        httpTaskManager.getTask(testTaskId1);
        httpTaskManager.getTask(testTaskId2);
        httpTaskManager.getTask(testTaskId1);

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = getResponse(request);

        List<Task> history = Arrays.asList(gson.fromJson(response.body(), Task[].class));
        assertEquals(history, httpTaskManager.getHistory());
    }

    @DisplayName("Добавления, удаление подзадачи.")
    @Test
    void shouldCreateAndDeleteSubtask() {
        httpTaskManager.createTask(testTask1);
        httpTaskManager.createEpic(testEpic2);

        URI url1 = URI.create("http://localhost:8080/tasks/subtask");
        String json1 = gson.toJson(testSubtask3);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(body1)
                .build();
        HttpResponse<String> response1 = getResponse(request1);

        assertTrue(testEpic2.getSubtaskIdList().contains(testSubtask3.getId()), "Не содержит id подзадачи.");

        URI url2 = URI.create("http://localhost:8080/tasks/subtask?id=" + testSubtask3.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .DELETE()
                .build();
        HttpResponse<String> response2 = getResponse(request2);
        assertTrue(testEpic2.getSubtaskIdList().isEmpty(), "Подзадача не удалена из SubtaskIdList.");

        URI url3 = URI.create("http://localhost:8080/tasks/subtask?id=" + testSubtask3.getId());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        HttpResponse<String> response3 = getResponse(request3);
        assertEquals(response3.statusCode(), 404, "Подзадача не удалена, проверка через запрос.");
    }

    @DisplayName("Обновление задачи/подзадачи/эпика.")
    @Test
    void shouldUpdateEpicTaskSubtask() {
        int anyId = httpTaskManager.createTask(testTask1);

        Task updateTestTask = new Task(anyId, Status.IN_PROGRESS, "обновленная_задача1", "описание_задачи1",
                Instant.now(), Duration.ofMinutes(10));

        URI url1 = URI.create("http://localhost:8080/tasks/task");
        String json1 = gson.toJson(updateTestTask);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .POST(body1)
                .build();
        HttpResponse<String> response1 = getResponse(request1);

        assertEquals(httpTaskManager.getTask(anyId), updateTestTask, "Задачи отличаются.");
    }

    @DisplayName("Проверка ответа, если история пустая.")
    @Test
    void complexTest() {
        httpTaskManager.createTask(testTask1);
        httpTaskManager.createEpic(testEpic2);
        httpTaskManager.createSubtask(testSubtask3);
        httpTaskManager.createSubtask(testSubtask4);
        httpTaskManager.createTask(testTask5);

        URI url1 = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();
        HttpResponse<String> response1 = getResponse(request1);
        assertEquals(response1.body(), "История пустая.");

        URI url2 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .DELETE()
                .build();
        HttpResponse<String> response2 = getResponse(request2);
        assertEquals(response2.body(), "Все задачи удалены.");

        assertTrue(httpTaskManager.getTasks().isEmpty(), "Список задач не очищен.");

        URI url3 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .DELETE()
                .build();
        HttpResponse<String> response3 = getResponse(request3);
        assertEquals(response3.body(), "Все подзадачи удалены.");

        assertTrue(testEpic2.getSubtaskIdList().isEmpty(), "Подзадачи не удалены из SubtaskIdList");
        assertTrue(httpTaskManager.getSubtasks().isEmpty(), "Список подзадач не очищен.");

        URI url4 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url4)
                .DELETE()
                .build();
        HttpResponse<String> response4 = getResponse(request4);
        assertTrue(httpTaskManager.getEpics().isEmpty(), "Список эпиков не очищен.");
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Во время выполнения запроса возникла ошибка. " +
                    "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Введённый вами адрес не соответствует формату URL. " +
                    "Попробуйте, пожалуйста, снова.");
        }
        return response;
    }
}