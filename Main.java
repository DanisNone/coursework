import server.Server;

public class Main {
    static public void main(String[] args) {
        String hostname = "0.0.0.0";
        int port = 8080;
        try {
            Server server = new Server(hostname, port);
            server.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
/*
        System.out.println("server start; http://" + hostname + ":" + port);

        URI uri = new URI("http://" + hostname + ":" + port + "?we=e");
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.getInputStream();*/
    }
}