public class LinkCollisionException extends  LinkException{
    public LinkCollisionException(){
        super("Failed to generate a unique code after 8 attempts.");
    }
}
