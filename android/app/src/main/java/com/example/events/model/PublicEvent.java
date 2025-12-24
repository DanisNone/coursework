package com.example.events.model;


public class PublicEvent {
    private String startTime;
    private String endTime;
    private String full_location;
    private String city;
    private String name;
    private String descr;
    private PublicUser publicUser;


    public PublicEvent() {
        this.startTime = "";
        this.endTime = "";
        this.full_location = "";
        this.city = "";
        this.name = "";
        this.descr = "";
        this.publicUser = new PublicUser();
    }

    public PublicEvent(PublicEvent publicEvent) {
        this.startTime = publicEvent.getStartTime();
        this.endTime = publicEvent.getEndTime();
        this.full_location = publicEvent.getFull_location();
        this.city = publicEvent.getCity();
        this.name = publicEvent.getName();
        this.descr = publicEvent.getDescription();
        this.publicUser = publicEvent.getPublicUser();
    }

    public PublicEvent(Event event, PublicUser user) {
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.full_location = event.getFull_location();
        this.city = event.getCity();
        this.name = event.getName();
        this.descr = event.getDescription();
        this.publicUser = user;
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
        return publicUser.getName();
    }

    public String getOwnerSurname() {
        return publicUser.getSurname();
    }
    public PublicUser getPublicUser() {
        return publicUser;
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
//    public void setOwnerName(String ownerName) {
//        publicUser.setName(ownerName);
//    }
//    public void setOwnerSurname(String ownerSurname) {
//        publicUser.setSurname(ownerSurname);
//    }
    public void setPublicUser(PublicUser publicUser) {
        this.publicUser = publicUser;
    }
}
