import server.Server;

public class Main {
    static public void main(String[] args) {
        String hostname = "193.108.113.136";
        int port = 8080;
        try {
            Server server = new Server(hostname, port);
            server.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
