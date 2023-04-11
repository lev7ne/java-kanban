package servers;

import exceptions.ManagerAuthException;
import exceptions.ManagerLoadException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String apiToken;
    private final int port;

    public KVTaskClient(int port) {
        this.port = port;
        URI uri = URI.create("http://localhost:" + port + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new ManagerAuthException("Ошибка при регистрации клиента");
        }

        if (response.statusCode() == 200) {
            apiToken = response.body();
        } else {
            throw new ManagerAuthException("Ошибка при регистрации клиента, код: " + response.statusCode());
        }
    }

    public void put(String key, String json) { // POST /save/<ключ>?API_TOKEN=
        URI uri = URI.create("http://localhost:" + port + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;

        try {
            client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при сохранении");
            throw new ManagerLoadException("Ошибка при сохранении состояния менеджера на сервер");
        }
    }

    public String load(String key) { // GET /load/<ключ>?API_TOKEN=
        URI uri = URI.create("http://localhost:" + port + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;

        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            throw new ManagerAuthException("Ошибка при регистрации клиента");
        }

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new ManagerAuthException("Ошибка при регистрации клиента, код: " + response.statusCode());
        }
    }
}
