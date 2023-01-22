package models;

import java.util.Objects;

public class Subtask extends Task {

    public Integer epicID;

    public Integer getEpicID() {
        return epicID;
    }

    public Subtask(String title, String description, String status) {
        super(title, description, status);
    }

    @Override
    public String toString() {
        return "models.Subtask{" +
                "epicID=" + epicID +
                ", id=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
