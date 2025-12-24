package com.example.events.model;

public class PublicUser {
    private final String name;
    private final String surname;

    public PublicUser(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

}