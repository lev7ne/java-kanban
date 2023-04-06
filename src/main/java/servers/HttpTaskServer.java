package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import managers.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private Gson gson;
    private HttpTaskManager httpTaskManager;

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        this.httpTaskManager = httpTaskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/", this::handle); // создаем HttpContext с обработчиком
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefaultHttpTaskManager());
        httpTaskServer.start();

        httpTaskServer.stop();
    }

    private void handle(HttpExchange httpExchange) throws IOException {

        String requestMethod = httpExchange.getRequestMethod(); // извлекаем метод
        String path = httpExchange.getRequestURI().getPath(); // извлекаем путь /tasks/task
        String query = httpExchange.getRequestURI().getQuery(); // извлекаем "строку запроса"


        String[] pathParts = path.split("/"); // делим путь

        switch (pathParts[2]) {
            case "task":

                break;
        }

        // дальше необходимо в зависимости от содержимого path[2] использовать какой-то handle (таск, эпик, сабтаск, история),
        // если pathPart.length == 2, ответ будет getPrioritizedTasks()
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