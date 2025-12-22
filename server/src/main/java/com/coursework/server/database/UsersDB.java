package com.coursework.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UsersDB {
    static private UsersDB instance;
    private final Connection conn;
    
    private UsersDB() throws SQLException {
        conn = Database.getInstance().getConnection();
    }

    static public UsersDB getInstance() throws SQLException {
        if (instance == null)
            instance = new UsersDB();
        return instance;
    }

    public long insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (login, password_hash, name, surname) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getSurname());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getLong(1);
                throw new SQLException("Failed to retrieve generated user id");
            }
        }
    }
}