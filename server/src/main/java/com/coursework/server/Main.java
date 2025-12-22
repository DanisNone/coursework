package com.coursework.server; 

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static public void main(String[] args) throws IOException, SQLException {
/*        UsersDB users;
        EventsDB events;
        
        users = UsersDB.getInstance();
        events = EventsDB.getInstance();

        long id1 = users.insertUser(User.fromPassword("Danis", "Password", "Danis", "Gubaydullin"));
        long id2 = users.insertUser(User.fromPassword("Panov", "Password", "Sasha", "Panov"));
        events.insertEvent(new Event(
            LocalDateTime.of(2025, 10, 11, 10, 0),
            LocalDateTime.of(2025, 10, 12, 10, 0),
            "QWERTY",
            "MOSCOW", 
            "Event", "best event",
            id1
        ));

        events.insertEvent(new Event(
            LocalDateTime.of(2025, 10, 11, 10, 0),
            LocalDateTime.of(2025, 10, 12, 10, 0),
            "QWERTY",
            "MOSCOW", 
            "Event2", "best event",
            id1
        ));

        events.insertEvent(new Event(
            LocalDateTime.of(2025, 10, 11, 10, 0),
            LocalDateTime.of(2025, 10, 12, 10, 0),
            "QWERTY",
            "MOSCOW", 
            "Event2", "best event",
            id2
        ));*/
        String hostname = "193.108.113.136";
        int port = 8080;
        Server server = new Server(hostname, port);
        server.start();
    }
}
