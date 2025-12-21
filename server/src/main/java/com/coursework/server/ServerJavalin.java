package com.coursework.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import com.coursework.server.database.Event;
import com.coursework.server.database.EventsBD;
import com.google.gson.Gson;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

class GetCitiesHadler implements Handler {
    @Override
    public void handle(Context ctx) {
        try {
            EventsBD eventsBD = EventsBD.get_instance();
            Gson gson = new Gson();
            List<String> cities = eventsBD.getAllCity();
            cities.add(0, Event.ANY_CITY);
            ctx.status(HttpStatus.OK);
            ctx.result(gson.toJson(cities).getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

public class ServerJavalin {
    private final String hostname;
    private final int port;
    private final Javalin server;

    public ServerJavalin(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;
        server = Javalin.create();
    }

    public void start() {
        server.start(hostname, port);
        server.get("/get_cities", new GetCitiesHadler());
    }

    public String getAddress() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

}