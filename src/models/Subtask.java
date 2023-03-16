package models;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description, Status status, Integer epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, Status status, String description,  Integer epicId) {
        super(id, title, status, description);
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
        return getId() + "," + TaskType.SUBTASK + "," + name + "," + this.status + "," + description + " " + name.toLowerCase() + "," + epicId;
    }
}