package ng.safebank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class AmountParserTest {

    @Test
    public void nonNumericInputIsRejectedWithHelpfulMessage() {
        try {
            AmountParser.parse("abc");
            fail("Expected InvalidAmountException");
        } catch (InvalidAmountException e) {
            assertTrue(e.getMessage().contains("not a valid number"));
        }
    }

    @Test
    public void emptyInputIsRejected() {
        try {
            AmountParser.parse("   ");
            fail("Expected InvalidAmountException");
        } catch (InvalidAmountException e) {
            assertTrue(e.getMessage().contains("empty"));
        }
    }

    @Test
    public void commasAndNairaSignAreAccepted() throws InvalidAmountException {
        BigDecimal result = AmountParser.parse("\u20A6100,000");
        assertEquals(0, new BigDecimal("100000").compareTo(result));
    }

    @Test
    public void decimalsAreAccepted() throws InvalidAmountException {
        BigDecimal result = AmountParser.parse("2500.50");
        assertEquals(0, new BigDecimal("2500.50").compareTo(result));
    }
}
