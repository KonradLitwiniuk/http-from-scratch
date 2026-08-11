import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting server");
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("Server started. Listening on port 8080");
            System.out.println("Connecting to database...");
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/scratch", "dev", "dev");
            LinkRepository lp = new LinkRepository(con);
            while (true) {
                try(Socket socket = ss.accept();BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));PrintWriter pw = new PrintWriter(socket.getOutputStream());) {
                    String line;
                    int cntLength = 0;
                    String[] lengthSplited;
                    String firstLine = reader.readLine();
                    System.out.println(firstLine);
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        System.out.println(line);
                        if(line.toLowerCase().contains("content-length")){
                            lengthSplited = line.trim().split(":");
                            cntLength = Integer.parseInt(lengthSplited[1].trim());
                            System.out.println(cntLength);
                        }
                    }
                    char[] buffer = new char[cntLength];
                    reader.read(buffer, 0, cntLength);
                    String body = new String(buffer);
                    System.out.println("BODY: " + body);
                    String[] splittedFirstLine = firstLine.split("[\\s]+");
                    String response;
                    if(splittedFirstLine[1].equals("/"))
                        response = buildResponse("200 OK", "<h1>Hello from Java</h1>");
                    else if(splittedFirstLine[1].equals("/about"))
                        response = buildResponse("200 OK", "<h1>About</h1>");
                    else if(splittedFirstLine[1].startsWith("/links/"))
                    {
                        String code = splittedFirstLine[1].substring("/links/". length());
                        Optional<Link> link = lp.findByCode(code);
                        if(link.isPresent())
                        {
                            response = buildRedirectResponse("302 Found", link.get().getUrl());
                        }
                        else
                        {
                            response = buildResponse("404 Not Found", "<h1>Link not found</h1>");
                        }
                    }
                    else
                         response = buildResponse("404 Not Found", "<h1>404 Not Found</h1>");
                    pw.print(response);
                    pw.flush();
                }
            }
        }
        catch(SQLException e){
            System.out.println("Server Error: " + e);
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
    public static String buildRedirectResponse(String status, String Location)
    {
        return "HTTP/1.1 " + status + "\r\n" +
                "Location: " + Location + "\r\n" + "\r\n";
    }
}