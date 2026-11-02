package ng.safebank;

import java.math.BigDecimal;

public class InsufficientFundsException extends SafeBankException {

    private final BigDecimal availableBalance;
    private final BigDecimal requestedAmount;

    public InsufficientFundsException(BigDecimal availableBalance, BigDecimal requestedAmount) {
        super("Insufficient funds. Available balance is " + Money.format(availableBalance)
                + " but you asked for " + Money.format(requestedAmount) + ".");
        this.availableBalance = availableBalance;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}
