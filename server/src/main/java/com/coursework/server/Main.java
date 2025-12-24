package com.coursework.server; 

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static public void main(String[] args) throws IOException, SQLException {
        String hostname = "193.108.113.136";
        int port = 8080;
        Server server = new Server(hostname, port);
        server.start();
    }
}
