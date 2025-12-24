package com.example.events.network;

import android.util.Log;

import com.example.events.model.Event;
import com.example.events.model.PublicEvent;
import com.example.events.model.PublicUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final Gson gson = new Gson();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public interface UserCallback {
        void onSuccess(PublicUser user);
        void onError(Exception e);
    }

    // Интерфейс для получения событий
//    public interface EventsCallback {
//        void onSuccess(List<Event> events);
//        void onError(Exception e);
//    }


    public interface EventsCallback {
        void onSuccess(List<PublicEvent> events);
        void onError(Exception e);
    }

    // Новый интерфейс для POST-запросов
    public interface PostCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    // Упрощенный интерфейс (если не нужен ответ)
    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void getEventsAsync(String city, String start, String end,
                                      EventsCallback callback) {
        executor.execute(() -> {
            try {
                String url = ApiConfig.buildEventsGetUrl(city, start, end);
                String json = httpGet(url);
                List<PublicEvent> events = parsePublicEvents(json);
                callback.onSuccess(events);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void getUserAsync(int userID, UserCallback callback) {
        executor.execute(() -> {
            try {
                String url = ApiConfig.buildUserGetUrl(userID);
                String json = httpGet(url);
                PublicUser user = parsePublicUsers(json);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void getUserAsync(String UserLogin, UserCallback callback) {
        executor.execute(() -> {
            try {
                String url = ApiConfig.buildUserGetUrl(UserLogin);
                String json = httpGet(url);
                PublicUser user = parsePublicUsers(json);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void postJsonAsync(String urlStr, String jsonData, PostCallback callback) {
        Log.e("JSON", jsonData);
        executor.execute(() -> {
            try {
                String response = httpPost(urlStr, jsonData);
                callback.onSuccess(response);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void postJsonAsync(String urlStr, String jsonData, SimpleCallback callback) {
        executor.execute(() -> {
            try {
                httpPost(urlStr, jsonData); // Просто отправляем, не обрабатываем ответ
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // Основной метод POST-запроса
    private static String httpPost(String urlStr, String jsonData) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            // Отправляем данные
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Получаем статус ответа
            int responseCode = conn.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                // Читаем успешный ответ
                try (InputStream is = conn.getInputStream();
                     Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                    StringBuilder response = new StringBuilder();
                    char[] buffer = new char[1024];
                    int charsRead;
                    while ((charsRead = reader.read(buffer)) != -1) {
                        response.append(buffer, 0, charsRead);
                    }
                    return response.toString();
                }
            } else {
                // Читаем ошибку
                String errorMessage;
                try (InputStream is = conn.getErrorStream();
                     Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                    StringBuilder error = new StringBuilder();
                    char[] buffer = new char[1024];
                    int charsRead;
                    while ((charsRead = reader.read(buffer)) != -1) {
                        error.append(buffer, 0, charsRead);
                    }
                    errorMessage = error.toString();
                } catch (Exception e) {
                    errorMessage = "HTTP Error " + responseCode;
                }
                throw new RuntimeException("HTTP " + responseCode + ": " + errorMessage);
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String httpGet(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream is = conn.getInputStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int n;
            while ((n = reader.read(buf)) > 0) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }

    private static List<Event> parseEvents(String json) {
        return gson.fromJson(json, new TypeToken<List<Event>>(){}.getType());
    }

    private static PublicUser parsePublicUsers(String json) {
        return gson.fromJson(json, PublicUser.class);
    }
    private static List<PublicEvent> parsePublicEvents(String json) {
        List<PublicEvent> resEvents = new ArrayList<>();
        List<Pair<Event, PublicUser>> pairedData = gson.fromJson(json, new TypeToken<List<Pair<Event, PublicUser>>>(){}.getType());
        for (Pair<Event, PublicUser> pair : pairedData) {
            Event event = pair.first;
            PublicUser publicUser = pair.second;
            resEvents.add(new PublicEvent(event, publicUser));
        }
        return resEvents;
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