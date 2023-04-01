package main.models;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIdList = new ArrayList<>();
    Instant endTime;

    public Epic(Integer id, Status status, String name, String description, Instant startTime, Duration duration, ArrayList<Integer> subtasksIds) {
        super(id, status, name, description, startTime, duration);
        this.subtaskIdList = subtasksIds;
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public Instant getEndTime() {
        endTime = startTime.plus(duration);
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", status=" + status +
                ", name=" + name +
                ", description=" + description +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() +
                ", subtaskIdList=" + subtaskIdList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtaskIdList.equals(epic.subtaskIdList) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList, endTime);
    }
}