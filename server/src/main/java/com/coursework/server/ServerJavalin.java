package com.coursework.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import com.coursework.server.database.Event;
import com.coursework.server.database.EventDeserializer;
import com.coursework.server.database.EventSerializer;
import com.coursework.server.database.EventsBD;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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

class GetEventsHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        LocalDateTime start = null, end = null;
        String city = null;
        
        Map<String, List<String>> params = ctx.queryParamMap();
        List<String> cities = params.get("city");
        if (cities != null && !cities.isEmpty()) city = cities.get(0);
        if (city != null && city.equalsIgnoreCase(Event.ANY_CITY)) city = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if (params.containsKey("start")) {
                start = LocalDateTime.parse(params.get("start").get(0), formatter);
            }
            if (params.containsKey("end")) {
                end = LocalDateTime.parse(params.get("end").get(0), formatter);
            }
        } catch (DateTimeParseException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Invalid date format. Use dd.MM.yyyy HH:mm".getBytes(StandardCharsets.UTF_8));
            return;
        }

        try {
            EventsBD eventsBD = EventsBD.get_instance();
            List<Event> events = eventsBD.getEvents(start, end, city);
            Gson gson = new GsonBuilder().registerTypeAdapter(Event.class, new EventSerializer()).create();
            String response = gson.toJson(events);
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class AddEventHandler implements Handler {

    @Override
    public void handle(Context ctx) {
        String body = ctx.body();

        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Event.class, new EventDeserializer()).create();
            Event event = gson.fromJson(body, new TypeToken<Event>(){}.getType());

            EventsBD eventsBD = EventsBD.get_instance();
            eventsBD.insertEvent(event);

            String response = "{\"status\":\"success\",\"message\":\"Event added\"}";
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (DateTimeParseException e) {
            String response = "{\"status\":\"error\",\"message\":\"Invalid date format. Use dd.MM.yyyy HH:mm\"}";
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            String response = "{\"status\":\"error\",\"message\":\"Invalid request body\"}";
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
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
        server.get("/get_events", new GetEventsHandler());
        server.post("/add_event", new AddEventHandler());
    }

    public String getAddress() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}