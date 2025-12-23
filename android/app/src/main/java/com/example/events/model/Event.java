package com.example.events.model;


public class Event {
    private String startTime;
    private String endTime;
    private String full_location;
    private String city;
    private String name;
    private String descr;
    private int ownerId;

    public Event(String startTime, String endTime, String full_location, String city, String name, String descr, int ownerId){
        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = name;
        this.descr = descr;
        this.ownerId = ownerId;
    }
    public Event(Event event) {
        this.startTime = event.getStartTime();
        this.endTime = event.endTime;
        this.full_location = event.getFull_location();
        this.city = event.getCity();
        this.name = event.getName();
        this.descr = event.getDescription();
        this.ownerId = event.getOwnerId();
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
    public int getOwnerId() {
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
    public void setOwnerId(int ownerId) {this.ownerId = ownerId; }
}
