package main.manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setupInMemoryTaskManager() {
        taskManager = (InMemoryTaskManager) Managers.getDefaultInMemoryTaskManager();
    }
}