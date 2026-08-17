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
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/scratch", "dev", "dev");
            LinkRepository linkRepository = new LinkRepository(con);
            LinkService linkService = new LinkService(linkRepository);
            HttpServer server = new HttpServer(linkService, 8080);
            server.startServer();
        }
        catch(Exception e){
            System.out.println("Server Error: " + e);
        }
    }

}