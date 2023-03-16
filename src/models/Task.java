package models;

public class Task {
    private Integer id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String title, String description, Status status) {
        this.name = title;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String title, Status status, String description) { //
        this.id = id;
        this.name = title;
        this.status = status;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.TASK + "," + name + "," + this.status + "," + description + " " + name.toLowerCase();
    }

}