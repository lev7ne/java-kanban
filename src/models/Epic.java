package models;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIdList = new ArrayList<>();

    public Epic (String title, String description, String status) {
        super(title, description, status);
    }

    public Epic(Integer id, String title, String description) {
        super(id, title, description);

    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subtaskIdList=" + subtaskIdList +
                '}';
    }

}