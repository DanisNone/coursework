package com.example.events.network;

import android.net.Uri;

import com.example.events.model.Event;

public class ApiConfig {
    public static final String BASE_URL = "http://193.108.113.136:8080/";
    public static final String ANY_CITY = "Любой";
    public static final String POST_STR = BASE_URL + "add_event/";

    static String buildEventsGetUrl(String city, String start, String end) {
        Uri.Builder builder = Uri.parse(BASE_URL + "get_events/").buildUpon();

        if (!ANY_CITY.equals(city)) {
            builder.appendQueryParameter("city", city);
        }
        if (!start.isEmpty()) {
            builder.appendQueryParameter("start", start);
        }
        if (!end.isEmpty()) {
            builder.appendQueryParameter("end", end);
        }
        return builder.build().toString();
    }
}
