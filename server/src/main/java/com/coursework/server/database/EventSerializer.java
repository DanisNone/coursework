package com.coursework.server.database;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventSerializer implements JsonSerializer<Event> {
    @Override
    public JsonElement serialize(final Event value, final Type type, final JsonSerializationContext context) {
        final JsonObject jsonObj = new JsonObject();
        jsonObj.add("startTime", context.serialize(value.startTime.toString()));
        jsonObj.add("endTime", context.serialize(value.endTime.toString()));
        jsonObj.add("full_location", context.serialize(value.full_location));
        jsonObj.add("city", context.serialize(value.city));
        jsonObj.add("name", context.serialize(value.name));
        jsonObj.add("descr", context.serialize(value.descr));

        return jsonObj;
    }
}