package ng.safebank;

import java.math.BigDecimal;

public class WithdrawalLimitExceededException extends SafeBankException {

    private final BigDecimal limit;
    private final BigDecimal requestedAmount;

    public WithdrawalLimitExceededException(BigDecimal limit, BigDecimal requestedAmount) {
        super("Withdrawal limit exceeded. The single-withdrawal limit is " + Money.format(limit)
                + " but you asked for " + Money.format(requestedAmount) + ".");
        this.limit = limit;
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
}
