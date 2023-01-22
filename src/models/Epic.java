package models;

import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addNewSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateEpicStatus();
    }

    public void updateEpicStatus() {
        for (Subtask element : subtasks) {
            if (!element.status.equals("DONE")) {
                this.status = "IN_PROGRESS";
            }
        }
    }

    public Epic(String title, String description) {
        super(title, description);
        this.status = "NEW";
    }

    @Override
    public String toString() {
        return "models.Epic{" +
                "subtasks=" + subtasks +
                ", id=" + getId() +
                ", title='" + description + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
