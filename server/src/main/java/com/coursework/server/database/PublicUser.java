package com.coursework.server.database;

public class PublicUser {
    private String name;
    private String surname;

    public PublicUser(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public PublicUser(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
}