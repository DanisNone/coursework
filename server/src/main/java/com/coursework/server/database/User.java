package com.coursework.server.database;

import org.mindrot.jbcrypt.BCrypt;


public class User {
    private final String login;
    private final String passwordHash;
    private final String name;
    private final String surname;

    public User(String login, String passwordHash, String name, String surname) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
    }

    public static User fromPassword(String login, String password, String name, String surname) {
        String salt = BCrypt.gensalt();
        String password_hash = BCrypt.hashpw(password, salt);

        return new User(login, password_hash, name, surname);
    }

    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, passwordHash);
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}