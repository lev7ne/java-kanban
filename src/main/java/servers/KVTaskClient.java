package servers;

import exceptions.ManagerAuthException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String apiToken;
    private final int port;

    public KVTaskClient(String url, int port) {
        this.url = url;
        this.port = port;
        URI uri = URI.create(url + port + "/register");
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
        apiToken = response.body();
    }

    public void put(String key, String json) { // POST /save/<ключ>?API_TOKEN=
        URI uri = URI.create(url + port + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
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
            System.out.println("Данные успешно загружены.");
        } else
            System.out.println("Произошла ошибка: " + response.statusCode());
    }

    public String load(String key) { // GET /load/<ключ>?API_TOKEN=
        URI uri = URI.create(url + port + "/load/" + key + "?API_TOKEN=" + apiToken);
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

        return response.body();
    }
}
