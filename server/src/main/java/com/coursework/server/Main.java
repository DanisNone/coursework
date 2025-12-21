package com.coursework.server; 

import java.io.IOException;

public class Main {
    static public void main(String[] args) throws IOException {
        String hostname = "10.25.248.179";
        int port = 8080;
        Server server = new Server(hostname, port);
        server.start();
    }
}
