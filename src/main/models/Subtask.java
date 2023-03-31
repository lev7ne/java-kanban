package main.models;

import java.time.Duration;
import java.time.Instant;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, Status status, String name, String description, Instant startTime, Duration duration, Integer epicId) {
        super(id, status, name, description, startTime, duration);
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
        return "Subtask{" +
                "id=" + id +
                ", status=" + status +
                ", name=" + name +
                ", description=" + description +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() +
                ", epicId=" + epicId +
                '}';
    }
}