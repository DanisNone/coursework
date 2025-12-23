package com.coursework.server;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import com.coursework.server.database.Event;
import com.coursework.server.database.EventDeserializer;
import com.coursework.server.database.EventSerializer;
import com.coursework.server.database.EventsDB;
import com.coursework.server.database.User;
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
            List<Event> events = eventsDB.getEvents(start, end, city);
            Gson gson = new GsonBuilder().registerTypeAdapter(Event.class, new EventSerializer()).create();
            String response = gson.toJson(events);
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
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

class GetUserHandler implements Handler {
    @Override
    public void handle(Context ctx) {
        Map<String, List<String>> params = ctx.queryParamMap();
        Integer id = null;
        String id_s = null;
        List<String> idList = params.get("id");
        if (idList != null && !idList.isEmpty()) id_s = idList.get(0);
        if (id_s != null) id = Integer.valueOf(id_s);
        
        if (id == null || id <= 0) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("incorrect id");
            return;
        }

        try {
            UsersDB usersDB = UsersDB.getInstance();
            User user = usersDB.getById(id);
            String response = new Gson().toJson(user);
            ctx.status(HttpStatus.OK);
            ctx.result(response.getBytes(StandardCharsets.UTF_8));
        } catch (SQLException e) {
            ctx.result(e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}