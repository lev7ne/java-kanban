package manager;
public class Managers {
    private static HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        TaskManager inMemoryTaskManager = new InMemoryTaskManager(historyManager);
        return inMemoryTaskManager;
    }
    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}