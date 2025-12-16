package database;

import java.time.LocalDateTime;

public class Event {
    LocalDateTime startTime;
    LocalDateTime endTime;
    String full_location;
    String city;
    String name;

    public Event(LocalDateTime startTime, LocalDateTime endTime, String full_location, String city, String eventName) {
        if (endTime.isEqual(startTime) || endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("endTime >= startTime");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = eventName;
    }

    @Override
    public String toString() {
        return "Event{" +
               "startTime=" + startTime +
               ", endTime=" + endTime +
               ", full_location='" + full_location + '\'' +
               ", city='" + city + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
    public String toJSON() {
        return String.format(
            "{\"startTime\":\"%s\",\"endTime\":\"%s\",\"full_location\":\"%s\",\"city\":\"%s\",\"name\":\"%s\"}",
            startTime, endTime, full_location, city, name
        );
    }
}
