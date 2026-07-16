import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server");
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("Server started. Listening on port 8080");
            while (true) {
                ss.accept();
            }
        }
        catch(Exception e){
            System.out.println("Server Error: " + e);
        }
    }
}