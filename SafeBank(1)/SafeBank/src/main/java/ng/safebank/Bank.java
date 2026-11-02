package ng.safebank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Bank {

    private final Map<String, Account> accounts;
    private int nextAccountNumber;

    public Bank() {
        this.accounts = new LinkedHashMap<String, Account>();
        this.nextAccountNumber = 1001;
    }

    public Account createAccount(String holderName, BigDecimal openingBalance) throws InvalidAccountException {
        String accountNumber = "SB" + nextAccountNumber;
        Account account = new Account(accountNumber, holderName, openingBalance);
        nextAccountNumber++;
        accounts.put(accountNumber, account);
        return account;
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null) {
            throw new AccountNotFoundException("");
        }
        Account account = accounts.get(accountNumber.trim().toUpperCase());
        if (account == null) {
            throw new AccountNotFoundException(accountNumber.trim());
        }
        return account;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<Account>(accounts.values());
    }
    public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws SafeBankException {
        Account from = findAccount(fromNumber);
        Account to = findAccount(toNumber);

        if (from == to) {
            throw new InvalidAccountException("You cannot transfer money to the same account.");
        }
        from.validateWithdrawal(amount);
        to.validateDeposit(amount);

        from.applyDebit(amount, TransactionType.TRANSFER_OUT);
        to.applyCredit(amount, TransactionType.TRANSFER_IN);
    }
}
