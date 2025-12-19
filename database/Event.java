package database;

import java.time.LocalDateTime;

public class Event {
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public String full_location;
    public String city;
    public String name;
    public String descr;

    public Event(LocalDateTime startTime, LocalDateTime endTime, String full_location, String city, String eventName, String descr) {
        if (endTime.isEqual(startTime) || endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("endTime >= startTime");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = eventName;
        this.descr = descr;
    }

    @Override
    public String toString() {
        return "Event{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", full_location='" + full_location + '\'' +
                ", city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                '}';
    }
    public String toJSON() {
        return String.format(
                "{\"startTime\":\"%s\",\"endTime\":\"%s\",\"full_location\":\"%s\",\"city\":\"%s\",\"name\":\"%s\", \"descr\": \"%s\"}",
                startTime, endTime, full_location, city, name, descr
        );
    }
    public static Event fromJSON(String json) {
        json = json.trim().substring(1, json.length() - 1);

        String[] parts = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String startTimeStr = null, endTimeStr = null, full_location = null, city = null, name = null, descr = null;

        for (String part : parts) {
            String[] keyValue = part.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            switch (key) {
                case "startTime": startTimeStr = value; break;
                case "endTime": endTimeStr = value; break;
                case "full_location": full_location = value; break;
                case "city": city = value; break;
                case "name": name = value; break;
                case "descr": descr = value; break;
            }
        }

        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr);

        return new Event(startTime, endTime, full_location, city, name, descr);
    }
}