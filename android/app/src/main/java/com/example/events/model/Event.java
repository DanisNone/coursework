package com.example.events.model;


public class Event {
    private String startTime;
    private String endTime;
    private String full_location;
    private String city;
    private String name;
    private String descr;
    private long ownerId;

    public Event(String startTime, String endTime, String full_location, String city, String name, String descr, long ownerId){
        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = name;
        this.descr = descr;
        this.ownerId = ownerId;
    }

    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getFull_location() {
        return full_location;
    }
    public String getCity() {
        return city;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return descr;
    }
    public long getOwnerId() {
        return ownerId;
    }
    public void setStartTime(String time) {
        this.startTime = time;
    }
    public void setEndTime(String time) {
        this.endTime = time;
    }
    public void setFull_location(String location) {
        this.full_location = location;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String descr) {
        this.descr = descr;
    }
    public void setOwnerId(long ownerId) {this.ownerId = ownerId; }
}
