package models;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Integer> subtaskIdList = new ArrayList<>();

    public Epic (String title, String description, String status, List<Integer> subtaskIdList) {
        super(title, description, status);
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
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subtaskIdList=" + subtaskIdList +
                '}';
    }
}
