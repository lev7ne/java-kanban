import java.util.Objects;

public class Subtask extends Task {

    Integer epicID;

    public Integer getEpicID() {
        return epicID;
    }

    public Subtask(String title, String description, String status, Epic epic) {
        super(title, description, status);
        this.epicID = epic.id;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
