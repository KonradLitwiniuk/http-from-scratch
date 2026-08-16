import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.util.Optional;

public class LinkService {
    LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }
    public Optional<String> findOriginalUrl(String code) throws SQLException {
        Optional<Link> link = linkRepository.findByCode(code);
        if(link.isPresent()){
            return Optional.of(link.get().getUrl());
        }
        else{
            return Optional.empty();
        }
    }
    public String shortenLink(String url) {
        for(int i = 0; i < 8; i++) {
            try {
                StringBuilder sb = new StringBuilder();
                String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                for (int j = 0; j < 6; j++) {
                    int randomIndex = (int) (Math.random() * 62);
                    sb.append(chars.charAt(randomIndex));
                }
                linkRepository.save(new Link(url, sb.toString()));
                return sb.toString();
            } catch (SQLException e) {
                if (!e.getSQLState().equals("23505"))
                    throw new LinkStorageException("Błąd podczas zapisu linku w bazie danych", e);

            }
        }
        throw new LinkCollisionException();
    }
}
