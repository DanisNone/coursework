package com.coursework.server;

import java.io.IOException;

import io.javalin.Javalin;


public class Server {
    private final String hostname;
    private final int port;
    private final Javalin server;

    public Server(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;
        server = Javalin.create();
    }

    public void start() {
        server.start(hostname, port);
        server.get("/get_cities", new GetCitiesHadler());
        server.get("/get_events", new GetEventsHandler());
        server.get("/get_user", new GetUserHandler());
        server.post("/add_event", new AddEventHandler());
    }

    public String getAddress() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}