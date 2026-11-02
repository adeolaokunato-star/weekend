package ng.safebank;

public class AccountNotFoundException extends SafeBankException {

    public AccountNotFoundException(String accountNumber) {
        super("No account found with account number '" + accountNumber + "'.");
    }
}
