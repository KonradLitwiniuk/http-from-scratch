import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class JdbsTest {
    public static void main(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/scratch", "dev", "dev");
        LinkRepository lp = new LinkRepository(con);
        Link link1 = new Link("https://github.com/KonradLitwiniuk", "abc123");
        lp.save(link1);
        System.out.println(link1.getId());
        System.out.println(link1.getCreatedAt());
        Optional<Link> linkFound = lp.findByCode("abc123");
        if(linkFound.isPresent()){
            Link found = linkFound.get();
            System.out.println(found.getId());
            System.out.println(found.getUrl());
            System.out.println(found.getCode());
            System.out.println(found.getCreatedAt());
        }
        else{
            System.out.println("Link Not Found");
        }
    }
}
