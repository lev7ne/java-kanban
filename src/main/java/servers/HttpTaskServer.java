package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import managers.Managers;
import managers.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        this.taskManager = httpTaskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/", this::handle); // создаем HttpContext с обработчиком
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefaultHttpTaskManager(PORT));
        httpTaskServer.start();
    }

    private void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod(); // извлекаем метод
        String path = httpExchange.getRequestURI().getPath(); // извлекаем путь /tasks/task
        String query = httpExchange.getRequestURI().getQuery(); // извлекаем "строку запроса"

        String[] pathParts = path.split("/"); // делим путь
        String response;

        if (pathParts.length == 2) {
            Set<Task> responsePrioritizedTasks = taskManager.getPrioritizedTasks();
            response = gson.toJson(responsePrioritizedTasks);
            writeResponse(httpExchange, response, 200);
        }
        switch (pathParts[2]) {
            case "task":
                handleTask(httpExchange, requestMethod, query);
                break;
            case "epic":
                handleEpic(httpExchange, requestMethod, query);
                break;
            case "subtask":
                handleSubtask(httpExchange, requestMethod, query);
                break;
            case "history":
                if ("GET".equals(requestMethod)) {
                    List<Task> history = taskManager.getHistory();
                    if (history.isEmpty()) {
                        writeResponse(httpExchange, "История пустая.", 200);
                    } else {
                        writeResponse(httpExchange, gson.toJson(history), 200);
                    }
                } else {
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
                }
                break;
        }
    }

    private int parseId(String query) {
        String[] str = query.split("=");
        try {
            return Integer.parseInt(str[1]);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void handleTask(HttpExchange httpExchange, String requestMethod, String query) throws IOException {
        String response;
        int id;
        if (query == null) {
            switch (requestMethod) {
                case "GET":
                    response = gson.toJson(taskManager.getTasks());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Task newTask = gson.fromJson(string, Task.class);
                    if (newTask == null) {
                        break;
                    }
                    Task task = (newTask.getId() != null) ? taskManager.getTask(newTask.getId()) : null;
                    if (task != null) {
                        taskManager.updateTask(newTask);
                        writeResponse(httpExchange, "Задача c id=" + newTask.getId() + " обновлена.", 200);
                    } else {
                        id = taskManager.createTask(newTask);
                        writeResponse(httpExchange, "Создана задача c id=" + id + ".", 200);
                    }
                    break;
                case "DELETE":
                    taskManager.deleteTasks();
                    writeResponse(httpExchange, "Все задачи удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            int anyId = parseId(query);
            if (anyId != -1) {
                if (taskManager.getTask(anyId) != null) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(taskManager.getTask(anyId)), 200);
                            break;
                        case "DELETE":
                            taskManager.deleteTask(anyId);
                            writeResponse(httpExchange, "Задача с id=" + anyId + " успешно удалена.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Задача с id=" + anyId + " не создана.", 404);
                }
            } else {
                writeResponse(httpExchange, "Получен неверный id=" + parseId(query) + ".", 404);
            }
        }
    }

    private void handleEpic(HttpExchange httpExchange, String requestMethod, String query) throws IOException {
        String response;
        int id;
        if (query == null) {
            switch (requestMethod) {
                case "GET":
                    response = gson.toJson(taskManager.getEpics());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Epic newEpic = gson.fromJson(string, Epic.class);
                    if (newEpic == null) {
                        break;
                    }
                    Epic epic = (newEpic.getId() != null) ? (Epic) taskManager.getEpic(newEpic.getId()) : null;
                    if (epic != null) {
                        taskManager.updateEpic(newEpic);
                        writeResponse(httpExchange, "Эпик c id=" + newEpic.getId() + " обновлен.", 200);
                    } else {
                        id = taskManager.createEpic(newEpic);
                        writeResponse(httpExchange, "Создан эпик c id=" + id + ".", 200);
                    }
                    break;
                case "DELETE":
                    taskManager.deleteEpics();
                    writeResponse(httpExchange, "Все эпики удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            int anyId = parseId(query);
            if (anyId != -1) {
                if (taskManager.getEpic(anyId) != null) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(taskManager.getEpic(anyId)), 200);
                            break;
                        case "DELETE":
                            taskManager.deleteEpic(anyId);
                            writeResponse(httpExchange, "Эпик с id=" + anyId + " успешно удален.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Получен неверный id=" + anyId + ".", 404);
                }
            } else {
                writeResponse(httpExchange, "Получен неверный id=" + anyId + ".", 404);
            }
        }
    }

    private void handleSubtask(HttpExchange httpExchange, String requestMethod, String query) throws IOException {
        String response;
        int id;
        if (query == null) {
            switch (requestMethod) {
                case "GET":
                    response = gson.toJson(taskManager.getSubtasks());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Subtask newSubtask = gson.fromJson(string, Subtask.class);
                    if (newSubtask == null) {
                        break;
                    }
                    Subtask subtask = (newSubtask.getId() != null) ? (Subtask) taskManager.getSubtask(newSubtask.getId()) : null;
                    if (subtask != null) {
                        taskManager.updateSubtask(newSubtask);
                        writeResponse(httpExchange, "Подзадача c id=" + newSubtask.getId() + " обновлена.", 200);
                    } else {
                        id = taskManager.createSubtask(newSubtask);
                        writeResponse(httpExchange, "Создана подзадача c id=" + id + ".", 200);
                    }
                    break;
                case "DELETE":
                    taskManager.deleteSubtasks();
                    writeResponse(httpExchange, "Все подзадачи удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            int anyId = parseId(query);
            if (anyId != -1) {
                if (taskManager.getSubtask(anyId) != null) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(taskManager.getSubtask(anyId)), 200);
                            break;
                        case "DELETE":
                            taskManager.deleteSubtask(anyId);
                            writeResponse(httpExchange, "Подзадача с id=" + anyId + " успешно удалена.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Получен неверный id=" + anyId + ".", 404);
                }
            } else {
                writeResponse(httpExchange, "Получен неверный id=" + anyId + ".", 404);
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановлен сервер на порту " + PORT);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(UTF_8);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}