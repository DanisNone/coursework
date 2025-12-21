package com.coursework.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class EventsBD {
    static private EventsBD instance;

    private Connection conn;
    private EventsBD() throws SQLException {
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

    static public EventsBD get_instance() throws SQLException {
        if (instance == null)
            instance = new EventsBD();
        return instance;
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time DATETIME NOT NULL," +
                "end_time DATETIME NOT NULL," +
                "full_location TEXT NOT NULL," +
                "city TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "descr TEXT NOT NULL)";
        conn.createStatement().execute(sql);
    }

    public void insertEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (start_time, end_time, full_location, city, name, descr) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(event.startTime));
            pstmt.setTimestamp(2, Timestamp.valueOf(event.endTime));
            pstmt.setString(3, event.full_location);
            pstmt.setString(4, event.city);
            pstmt.setString(5, event.name);
            pstmt.setString(6, event.descr);
            pstmt.executeUpdate();
        }
    }

    public List<String> getAllCity() throws SQLException {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT city FROM events";
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        }
        return cities;
    }

    private List<Event> getEventsFromRS(ResultSet rs) throws SQLException {
        List<Event> events = new ArrayList<>();
        while (rs.next()) {
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
            String full_location = rs.getString("full_location");
            String event_city = rs.getString("city");
            String name = rs.getString("name");
            String descr = rs.getString("descr");
            Event event = new Event(startTime, endTime, full_location, event_city, name, descr);
            events.add(event);
        }
        return events;
    }
    public List<Event> getEvents(LocalDateTime start, LocalDateTime end, String city) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM events WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (start != null) {
            sql.append(" AND start_time >= ?");
            params.add(Timestamp.valueOf(start));
        }
        if (end != null) {
            sql.append(" AND end_time <= ?");
            params.add(Timestamp.valueOf(end));
        }
        if (city != null) {
            sql.append(" AND city = ?");
            params.add(city);
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                return getEventsFromRS(rs);
            }
        }
    }

}