package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import managers.Managers;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private Gson gson;
    private HttpTaskManager httpTaskManager;
    private static String key = "1";

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        this.httpTaskManager = httpTaskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/", this::handle); // создаем HttpContext с обработчиком
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefaultHttpTaskManager("http://localhost:", PORT, key));
        httpTaskServer.start();
        //httpTaskServer.stop();
    }

    private void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod(); // извлекаем метод
        String path = httpExchange.getRequestURI().getPath(); // извлекаем путь /tasks/task
        String query = httpExchange.getRequestURI().getQuery(); // извлекаем "строку запроса"

        String[] pathParts = path.split("/"); // делим путь
        //http://localhost:8080/tasks/task
        String response;

        if (pathParts.length == 2) {
            Set<Task> responsePrioritizedTasks = httpTaskManager.getPrioritizedTasks();
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
                if (httpTaskManager.getHistory().isEmpty()) {
                    writeResponse(httpExchange, "История пустая.", 200);
                } else {
                    writeResponse(httpExchange, gson.toJson(httpTaskManager.getHistory()), 200);
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
                    response = gson.toJson(httpTaskManager.getTasks());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Task newTask = gson.fromJson(string, Task.class);
                    if (newTask == null) {
                        break;
                    }
                    if (httpTaskManager.getTasks().size() != 0) {
                        for (Task task : httpTaskManager.getTasks()) {
                            if (task.getId() == newTask.getId()) {
                                httpTaskManager.updateTask(newTask);
                                writeResponse(httpExchange, "Задача c id=" + newTask.getId() + " обновлена.", 200);
                            } else {
                                id = httpTaskManager.createTask(newTask);
                                writeResponse(httpExchange, "Создана задача c id=" + id + ".", 200);
                                break;
                            }
                        }
                    } else {
                        id = httpTaskManager.createTask(newTask);
                        writeResponse(httpExchange, "Создана задача c id=" + id + ".", 200);
                    }
                    break;
                case "DELETE":
                    httpTaskManager.deleteTasks();
                    writeResponse(httpExchange, "Все задачи удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            if (parseId(query) != -1) {
                if (httpTaskManager.getTaskMap().containsKey(parseId(query))) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(httpTaskManager.getTask(parseId(query))), 200);
                            break;
                        case "DELETE":
                            httpTaskManager.deleteTask(parseId(query));
                            writeResponse(httpExchange, "Задача с id=" + parseId(query) + " успешно удалена.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Задача с id=" + parseId(query) + " не создана.", 404);
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
                    response = gson.toJson(httpTaskManager.getEpics());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Epic newEpic = gson.fromJson(string, Epic.class);
                    if (newEpic == null) {
                        break;
                    }
                    if (httpTaskManager.getEpics().size() != 0) {
                        for (Epic epic : httpTaskManager.getEpics()) {
                            if (epic.getId() == newEpic.getId()) {
                                httpTaskManager.updateEpic(newEpic);
                                writeResponse(httpExchange, "Эпик c id=" + newEpic.getId() + " обновлен.", 200);
                            } else {
                                id = httpTaskManager.createEpic(newEpic);
                                writeResponse(httpExchange, "Создан эпик c id=" + id + ".", 200);
                                break;
                            }
                        }
                    } else {
                        id = httpTaskManager.createEpic(newEpic);
                        writeResponse(httpExchange, "Создан эпик c id=" + id + ".", 200);
                    }
                    break;
                case "DELETE":
                    httpTaskManager.deleteEpics();
                    writeResponse(httpExchange, "Все эпики удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            if (parseId(query) != -1) {
                if (httpTaskManager.getEpicMap().containsKey(parseId(query))) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(httpTaskManager.getEpic(parseId(query))), 200);
                            break;
                        case "DELETE":
                            httpTaskManager.deleteEpic(parseId(query));
                            writeResponse(httpExchange, "Эпик с id=" + parseId(query) + " успешно удален.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Получен неверный id=" + parseId(query) + ".", 404);
                }
            } else {
                writeResponse(httpExchange, "Получен неверный id=" + parseId(query) + ".", 404);
            }
        }
    }

    private void handleSubtask(HttpExchange httpExchange, String requestMethod, String query) throws IOException {
        String response;
        int id;
        if (query == null) {
            switch (requestMethod) {
                case "GET":
                    response = gson.toJson(httpTaskManager.getSubtasks());
                    writeResponse(httpExchange, response, 200);
                    break;
                case "POST":
                    String string = new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
                    Subtask newSubtask = gson.fromJson(string, Subtask.class);
                    if (newSubtask == null) {
                        break;
                    } else {
                        if (httpTaskManager.getSubtasks().size() != 0) {
                            for (Subtask subtask : httpTaskManager.getSubtasks()) {
                                if (subtask.getId() == newSubtask.getId()) {
                                    httpTaskManager.updateSubtask(newSubtask);
                                    writeResponse(httpExchange, "Подзадача c id=" + newSubtask.getId() + " обновлена.", 200);
                                } else {
                                    id = httpTaskManager.createSubtask(newSubtask);
                                    writeResponse(httpExchange, "Создана подзадача c id=" + id + ".", 200);
                                    break;
                                }
                            }
                        } else {
                            id = httpTaskManager.createSubtask(newSubtask);
                            writeResponse(httpExchange, "Создана подзадача c id=" + id + ".", 200);
                            return;
                        }
                    }
                    break;
                case "DELETE":
                    httpTaskManager.deleteSubtasks();
                    writeResponse(httpExchange, "Все подзадачи удалены.", 200);
                    break;
                default:
                    writeResponse(httpExchange, "Метод не поддерживается.", 404);
            }
        } else {
            if (parseId(query) != -1) {
                if (httpTaskManager.getSubtaskMap().containsKey(parseId(query))) {
                    switch (requestMethod) {
                        case "GET":
                            writeResponse(httpExchange, gson.toJson(httpTaskManager.getSubtask(parseId(query))), 200);
                            break;
                        case "DELETE":
                            httpTaskManager.deleteSubtask(parseId(query));
                            writeResponse(httpExchange, "Подзадача с id=" + parseId(query) + " успешно удалена.", 200);
                            break;
                        default:
                            writeResponse(httpExchange, "Метод не поддерживается.", 404);
                    }
                } else {
                    writeResponse(httpExchange, "Получен неверный id=" + parseId(query) + ".", 404);
                }
            } else {
                writeResponse(httpExchange, "Получен неверный id=" + parseId(query) + ".", 404);
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