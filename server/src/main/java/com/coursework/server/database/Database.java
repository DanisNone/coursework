package com.coursework.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    static private Database instance;
    private final Connection conn;

    private Database() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("SQLite JDBC Driver not found");
        }
        String url = "jdbc:sqlite:database.db";
        conn = DriverManager.getConnection(url);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT NOT NULL UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "surname TEXT NOT NULL)";
    
        String events = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time DATETIME NOT NULL," +
                "end_time DATETIME NOT NULL," +
                "full_location TEXT NOT NULL," +
                "city TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "descr TEXT NOT NULL, " + 
                "owner_id INTEGER NOT NULL, " + 
                "FOREIGN KEY (owner_id) REFERENCES users(id))";

        conn.createStatement().execute(users);
        conn.createStatement().execute(events);
    }

    public static Database getInstance() throws SQLException {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }
}