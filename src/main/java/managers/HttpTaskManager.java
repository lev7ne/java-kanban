package managers;

import com.google.gson.Gson;
import servers.KVTaskClient;

public class HttpTaskManager extends FileBackedTasksManager {
    private String key;
    private KVTaskClient kvTaskClient;

    HttpTaskManager(String url, int port, String key) {
        kvTaskClient = new KVTaskClient(url, port);
        this.key = key;
        load();
    }

    @Override
    public void save() {
        String json = new Gson().toJson(this);
        kvTaskClient.put(key, json);
    }

    private void load() {
        kvTaskClient.load(key);
    }
}