package com.coursework.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class EventsDB {
    static private EventsDB instance;
    private final Connection conn;
    
    private EventsDB() throws SQLException {
        conn = Database.getInstance().getConnection();
    }

    static public EventsDB getInstance() throws SQLException {
        if (instance == null)
            instance = new EventsDB();
        return instance;
    }

    public void insertEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (start_time, end_time, full_location, city, name, descr, owner_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(event.startTime));
            pstmt.setTimestamp(2, Timestamp.valueOf(event.endTime));
            pstmt.setString(3, event.full_location);
            pstmt.setString(4, event.city);
            pstmt.setString(5, event.name);
            pstmt.setString(6, event.descr);
            pstmt.setLong(7, event.ownerId);
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
            long ownerId = rs.getLong("owner_id");
            Event event = new Event(startTime, endTime, full_location, event_city, name, descr, ownerId);
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