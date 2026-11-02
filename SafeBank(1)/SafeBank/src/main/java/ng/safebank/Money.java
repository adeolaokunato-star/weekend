package ng.safebank;

import java.math.BigDecimal;

public class Money {

    private Money() {
    }

    public static String format(BigDecimal amount) {
        return String.format("\u20A6%,.2f", amount);
    }
}
