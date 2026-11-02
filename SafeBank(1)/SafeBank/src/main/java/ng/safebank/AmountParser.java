package ng.safebank;

import java.math.BigDecimal;
public class AmountParser {

    private AmountParser() {
    }

    public static BigDecimal parse(String text) throws InvalidAmountException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidAmountException("Amount cannot be empty. Please type a number, for example 5000.");
        }

        String cleaned = text.trim();
        cleaned = cleaned.replace("\u20A6", "");
        cleaned = cleaned.replace(",", "");
        cleaned = cleaned.replace(" ", "");

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new InvalidAmountException(text.trim() + "' is not a valid number. Please type digits only, for example 5000 or 2500.50.");
        }
    }
}
