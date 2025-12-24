package com.coursework.server; 

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static public void main(String[] args) throws IOException, SQLException {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        Server server = new Server(hostname, port);
        server.start();
    }
}
