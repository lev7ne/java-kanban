package manager;
public class Managers {

    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultInMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }


}