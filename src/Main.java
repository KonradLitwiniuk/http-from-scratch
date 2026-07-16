import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server");
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("Server started. Listening on port 8080");
            while (true) {
                try(Socket socket = ss.accept();BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                    String line;
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        System.out.println(line);
                    }
                }

            }
        }
        catch(Exception e){
            System.out.println("Server Error: " + e);
        }
    }
}