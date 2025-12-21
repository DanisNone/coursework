package com.coursework.server.database;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EventDeserializer implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject obj = json.getAsJsonObject();
        LocalDateTime startTime = LocalDateTime.parse(obj.getAsJsonPrimitive("startTime").getAsString(), Event.formatter);
        LocalDateTime endTime = LocalDateTime.parse(obj.getAsJsonPrimitive("endTime").getAsString(), Event.formatter);
        String full_location = obj.getAsJsonPrimitive("full_location").getAsString();
        String city = obj.getAsJsonPrimitive("city").getAsString();
        String name = obj.getAsJsonPrimitive("name").getAsString();
        String descr = obj.getAsJsonPrimitive("descr").getAsString();
        
        return new Event(startTime, endTime, full_location, city, name, descr);
    }
}