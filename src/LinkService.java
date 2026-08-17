import org.postgresql.util.PSQLException;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Optional;

public class LinkService {
    private final LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }
    public String findOriginalUrl(String code) {
        try {
            Optional<Link> link = linkRepository.findByCode(code);
            if (link.isPresent()) {
                return link.get().getUrl();
            } else {
                throw new LinkNotFoundException();
            }
        } catch (SQLException e) {
            throw new LinkStorageException("Failed to retrieve link from the database", e);
        }
    }
    public String shortenLink(String url) {
        SecureRandom rand = new SecureRandom();
        for(int i = 0; i < 8; i++) {
            try {
                StringBuilder sb = new StringBuilder();
                String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                for (int j = 0; j < 6; j++) {
                    int randomIndex = rand.nextInt(62);
                    sb.append(chars.charAt(randomIndex));
                }
                linkRepository.save(new Link(url, sb.toString()));
                return sb.toString();
            } catch (SQLException e) {
                if (!e.getSQLState().equals("23505"))
                    throw new LinkStorageException("Failed to save link to the database", e);

            }
        }
        throw new LinkCollisionException();
    }
}
