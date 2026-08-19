import java.sql.Connection;
import java.sql.DriverManager;

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