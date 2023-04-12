package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Managers {

    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
    public static HttpTaskManager getDefaultHttpTaskManager(int port) {
        return new HttpTaskManager(port, false);
    }
    public static HistoryManager getDefaultInMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }
}