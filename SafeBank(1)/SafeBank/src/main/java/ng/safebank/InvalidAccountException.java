package ng.safebank;

public class InvalidAccountException extends SafeBankException {

    public InvalidAccountException(String message) {
        super(message);
    }
}
