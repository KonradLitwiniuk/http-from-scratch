import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server");
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("Server started. Listening on port 8080");
            while (true) {
                try(Socket socket = ss.accept();BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));PrintWriter pw = new PrintWriter(socket.getOutputStream());) {
                    String line;
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        System.out.println(line);
                    }
                    String html = "<h1>Hello from Java</h1>";
                    pw.print("HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: "+html.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" + html
                           );
                    pw.flush();
                }

            }
        }
        catch(Exception e){
            System.out.println("Server Error: " + e);
        }
    }
}