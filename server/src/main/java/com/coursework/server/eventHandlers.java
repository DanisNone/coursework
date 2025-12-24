package com.coursework.server;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.coursework.server.database.Event;
import com.coursework.server.database.EventDeserializer;
import com.coursework.server.database.EventSerializer;
import com.coursework.server.database.EventsDB;
import com.coursework.server.database.PublicUser;
import com.coursework.server.database.UsersDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;


class GetCitiesHadler implements Handler {
    @Override
    public void handle(Context ctx) {
        try {
            EventsDB eventsDB = EventsDB.getInstance();
            Gson gson = new Gson();
            List<String> cities = eventsDB.getAllCity();
            cities.add(0, Event.ANY_CITY);
            ctx.status(HttpStatus.OK);
            ctx.result(gson.toJson(cities).getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class Pair<X, Y> { 
    public final X first; 
    public final Y second; 
    public Pair(X first, Y second) { 
        this.first = first;
        this.second = second; 
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
            if (params.containsKey("start")) {
                start = LocalDateTime.parse(params.get("start").get(0), Event.formatter);
            }
            if (params.containsKey("end")) {
                end = LocalDateTime.parse(params.get("end").get(0), Event.formatter);
            }
        } catch (DateTimeParseException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Invalid date format. Use dd.MM.yyyy HH:mm".getBytes(StandardCharsets.UTF_8));
            return;
        }

        try {
            EventsDB eventsDB = EventsDB.getInstance();
            UsersDB usersDB = UsersDB.getInstance();
            List<Event> events = eventsDB.getEvents(start, end, city);
            List<Pair<Event, PublicUser>> response = new ArrayList<>();
            for (Event event: events) {
                PublicUser user = new PublicUser(usersDB.getById(event.ownerId));
                response.add(new Pair(event, user));
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Event.class, new EventSerializer()).create();
            String response_s = gson.toJson(response);
            ctx.status(HttpStatus.OK);
            ctx.result(response_s.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
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

            EventsDB eventsDB = EventsDB.getInstance();
            eventsDB.insertEvent(event);

            String response = "{\"status\":\"success\",\"message\":\"Event added\"}";
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (DateTimeParseException e) {
            String response = "{\"status\":\"error\",\"message\":\"Invalid date format. Use dd.MM.yyyy HH:mm\"}";
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            String response = "{\"status\":\"error\",\"message\":\"Invalid request body\"}";
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
