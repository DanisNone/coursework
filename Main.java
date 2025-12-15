import java.io.IOException;
import server.Server;

public class Main {
    static public void main(String[] args) throws IOException {
        String hostname = "127.0.0.1";
        int port = 8000;

        Server server = new Server(hostname, port);
        server.start();
/*
        System.out.println("server start; http://" + hostname + ":" + port);

        URI uri = new URI("http://" + hostname + ":" + port + "?we=e");
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.getInputStream();*/
    }
}