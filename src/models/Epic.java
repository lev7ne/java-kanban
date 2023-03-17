package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public Epic(Integer id, String title, Status status, String description) {
        super(id, title, status, description);
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.EPIC + "," + name + "," + this.status + "," + description + " " + name.toLowerCase() + "," + subtaskIdList;
    }
}