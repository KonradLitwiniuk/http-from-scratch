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
                    String firstLine = reader.readLine();
                    System.out.println(firstLine);
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        System.out.println(line);
                    }
                    String[] splittedFirstLine = firstLine.split("[\\s]+");
                    String response;
                    if(splittedFirstLine[1].equals("/"))
                        response = buildResponse("200 OK", "<h1>Hello from Java</h1>");
                    else if(splittedFirstLine[1].equals("/about"))
                        response = buildResponse("200 OK", "<h1>About</h1>");
                    else
                        response = buildResponse("404 Not Found", "<h1>404 Not Found</h1>");
                    pw.print(response);
                    pw.flush();
                }

            }
        }
        catch(Exception e){
            System.out.println("Server Error: " + e);
        }
    }
    public static String buildResponse(String status, String body){
        return "HTTP/1.1 "+ status +"\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: "+ body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + body;
    }
}