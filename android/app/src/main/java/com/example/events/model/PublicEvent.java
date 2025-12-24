package com.example.events.model;


public class PublicEvent {
    private String startTime;
    private String endTime;
    private String full_location;
    private String city;
    private String name;
    private String descr;
    private String ownerName;
    private String ownerSurname;

    public PublicEvent(String startTime, String endTime,
                       String full_location, String city,
                       String name, String descr,
                       String ownerName, String ownerSurname)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.full_location = full_location;
        this.city = city;
        this.name = name;
        this.descr = descr;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
    }

    public PublicEvent(Event event, PublicUser user) {
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.full_location = event.getFull_location();
        this.city = event.getCity();
        this.name = event.getName();
        this.descr = event.getDescription();
        this.ownerName = user.getName();
        this.ownerSurname = user.getSurname();
    }

    public PublicEvent() {
        this.startTime = "";
        this.endTime = "";
        this.full_location = "";
        this.city = "";
        this.name = "";
        this.descr = "";
        this.ownerName = "";
        this.ownerSurname = "";
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

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerSurname() {
        return ownerSurname;
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
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public void setOwnerSurname(String ownerSurname) {
        this.ownerSurname = ownerSurname;
    }
}
