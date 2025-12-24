package com.example.events.model;

public class PublicUser {
    private final long id;
    private final String name;
    private final String surname; 

    public PublicUser() {
        id = 0;
        this.name = "";
        this.surname = "";
    }

    public PublicUser(PublicUser publicUser) {
        this.id = publicUser.id;
        this.name = publicUser.getName();
        this.surname = publicUser.getSurname();
    }

    public PublicUser(long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public long getId() {
        return id;
    }
}