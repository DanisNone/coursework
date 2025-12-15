package server;

import com.sun.net.httpserver.*;
import database.Event;
import database.EventsBD;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class GetEventsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI requestURI = exchange.getRequestURI();
        Map<String, String> params = parseQuery(requestURI.getQuery());

        LocalDateTime start = null;
        LocalDateTime end = null;
        String city = params.get("city");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            if (params.containsKey("start")) {
                start = LocalDateTime.parse(params.get("start"), formatter);
            }
            if (params.containsKey("end")) {
                end = LocalDateTime.parse(params.get("end"), formatter);
            }
        } catch (DateTimeParseException e) {
            exchange.sendResponseHeaders(400, 0);
            OutputStream os = exchange.getResponseBody();
            os.write("Invalid date format. Use yyyy-MM-ddTHH:mm".getBytes());
            os.close();
            return;
        }

        try {
            EventsBD eventsBD = EventsBD.get_instance();
            List<Event> events = eventsBD.getEvents(start, end, city);
            String response = "[" + events.stream()
                    .map(Event::toJSON)
                    .collect(Collectors.joining(",")) + "]";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }

    private static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }
        return Map.ofEntries(
                java.util.Arrays.stream(query.split("&"))
                        .map(s -> URLDecoder.decode(s, StandardCharsets.UTF_8))
                        .map(s -> s.split("=", 2))
                        .map(arr -> Map.entry(arr[0], arr.length > 1 ? arr[1] : ""))
                        .toArray(Map.Entry[]::new)
        );
    }
}


class GetCitiesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            EventsBD eventsBD = EventsBD.get_instance();
            List<String> cities = eventsBD.getAllCity();
            String response = cities.stream().collect(Collectors.joining(","));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }
}
public class Server {

    private InetSocketAddress socketAddress;
    private HttpServer httpServer;

    public Server(String hostname, int port) throws IOException {
        socketAddress = new InetSocketAddress(hostname, port);
        httpServer = HttpServer.create(socketAddress, 0);

        httpServer.createContext("/get_events", new GetEventsHandler());
        httpServer.createContext("/get_cities", new GetCitiesHandler());
    }

    public void start() {
        httpServer.start();
        System.out.println("Server started on http:/" + getAddress() + ":" + getPort());
    }

    public String getAddress() {
        return socketAddress.getAddress().toString();
    }

    public int getPort() {
        return socketAddress.getPort();
    }

}
