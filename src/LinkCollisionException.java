public class LinkCollisionException extends  LinkException{
    public LinkCollisionException(){
        super("Nie udało się wygenerować unikalnego kodu po 8 próbach.");
    }
}
