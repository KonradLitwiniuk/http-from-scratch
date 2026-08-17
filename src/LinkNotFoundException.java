public class LinkNotFoundException extends LinkException{
    public LinkNotFoundException(){
        super("Link not found in database.");
    }
}
