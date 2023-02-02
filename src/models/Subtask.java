package models;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description, Status status, Integer epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, Status status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{"  +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}