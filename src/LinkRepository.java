import java.sql.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class LinkRepository {
    Connection connection;
    public LinkRepository(Connection connection){
        this.connection = connection;
    }
    public void save(Link link) throws SQLException {
        String sql = "INSERT INTO links (id, url, code, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pS = connection.prepareStatement(sql)) {
            pS.setObject(1, link.getId());
            pS.setObject(2, link.getUrl());
            pS.setObject(3, link.getCode());
            pS.setObject(4, Timestamp.from(link.getCreatedAt()));
            pS.executeUpdate();
        }
    }
    public Optional<Link> findByCode(String code) throws SQLException {
        String sql = "SELECT id, url, code, created_at FROM links WHERE code = ?";
        try (PreparedStatement pS = connection.prepareStatement(sql)) {
            pS.setObject(1, code);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                return Optional.of(new Link(rS.getObject("id", UUID.class), rS.getTimestamp("created_at").toInstant() ,rS.getString("url"), rS.getString("code")));
            } else {
                return Optional.empty();
            }
        }
    }
}
