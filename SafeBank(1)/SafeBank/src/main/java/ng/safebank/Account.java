package ng.safebank;

import java.math.BigDecimal;

public class Account {

    public static final BigDecimal SINGLE_WITHDRAWAL_LIMIT = new BigDecimal("100000");

    private final String accountNumber;
    private final String holderName;
    private BigDecimal balance;
    private static final int INITIAL_CAPACITY = 10;
    private Transaction[] history;
    private int historyCount;

    public Account(String accountNumber, String holderName, BigDecimal openingBalance) throws InvalidAccountException {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new InvalidAccountException("Account holder's name must not be blank.");
        }
        if (openingBalance == null) {
            throw new InvalidAccountException("Opening balance is required.");
        }
        if (openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAccountException("Opening balance must not be negative.");
        }
        if (openingBalance.stripTrailingZeros().scale() > 2) {
            throw new InvalidAccountException("Opening balance can have at most 2 decimal places.");
        }

        this.accountNumber = accountNumber;
        this.holderName = holderName.trim();
        this.balance = openingBalance.setScale(2);
        this.history = new Transaction[INITIAL_CAPACITY];
        this.historyCount = 0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Transaction[] getHistory() {
        Transaction[] copy = new Transaction[historyCount];
        for (int i = 0; i < historyCount; i++) {
            copy[i] = history[i];
        }
        return copy;
    }

    public int getHistoryCount() {
        return historyCount;
    }

    public void validateDeposit(BigDecimal amount) throws InvalidAmountException {
        validateAmount(amount);
    }

    public void validateWithdrawal(BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException, WithdrawalLimitExceededException {
        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(balance, amount);
        }
        if (amount.compareTo(SINGLE_WITHDRAWAL_LIMIT) > 0) {
            throw new WithdrawalLimitExceededException(SINGLE_WITHDRAWAL_LIMIT, amount);
        }
    }

    private void validateAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null) {
            throw new InvalidAmountException("Amount is required.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero. You entered " + amount.toPlainString() + ".");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new InvalidAmountException("Amount can have at most 2 decimal places.");
        }
    }

    // validate first, then change
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        validateDeposit(amount);
        applyCredit(amount, TransactionType.DEPOSIT);
    }

    public void withdraw(BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException, WithdrawalLimitExceededException {
        validateWithdrawal(amount);
        applyDebit(amount, TransactionType.WITHDRAWAL);
    }


    void applyCredit(BigDecimal amount, TransactionType type) {
        balance = balance.add(amount).setScale(2);
        addToHistory(new Transaction(type, amount.setScale(2), balance));
    }

    void applyDebit(BigDecimal amount, TransactionType type) {
        balance = balance.subtract(amount).setScale(2);
        addToHistory(new Transaction(type, amount.setScale(2), balance));
    }

    private void addToHistory(Transaction transaction) {
        if (historyCount == history.length) {
            growHistory();
        }
        history[historyCount] = transaction;
        historyCount++;
    }

    private void growHistory() {
        Transaction[] bigger = new Transaction[history.length * 2];
        for (int yomi = 0; yomi < history.length; yomi++) {
            bigger[yomi] = history[yomi];
        }
        history = bigger;
    }
}
