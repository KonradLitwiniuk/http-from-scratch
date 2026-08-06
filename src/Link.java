import java.time.Instant;
import java.util.UUID;

public class Link {
    UUID id;
    String url;
    String code;
    Instant createdAt;
    public Link(String url, String code){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.url = url;
        this.code = code;
    }
    public Link(UUID id, Instant createdAt,String url, String code){
        this.id = id;
        this.createdAt = createdAt;
        this.url = url;
        this.code = code;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getCode() {
        return code;
    }
}
