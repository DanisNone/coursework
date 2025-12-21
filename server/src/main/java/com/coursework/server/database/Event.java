package com.coursework.server.database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    static public final String ANY_CITY = "Любой";
    static public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public String full_location;
    public String city;
    public String name;
    public String descr;

    public Event(LocalDateTime startTime, LocalDateTime endTime, String full_location, String city, String name, String descr) {
        if (endTime.isEqual(startTime) || endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("endTime >= startTime");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = name;
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
}
