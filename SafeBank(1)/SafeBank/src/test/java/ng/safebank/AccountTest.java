package ng.safebank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountTest {

    private Account account;

    @BeforeEach
    public void setUp() throws InvalidAccountException {
        account = new Account("SB1001", "Yomi", new BigDecimal("200000"));
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    // Acc creation
    @Test
    public void blankNameIsRejected() {
        try {
            new Account("SB1", "   ", new BigDecimal("100"));
            fail("Expected InvalidAccountException");
        } catch (InvalidAccountException e) {
            assertTrue(e.getMessage().contains("name"));
        }
    }

    @Test
    public void negativeOpeningBalanceIsRejected() {
        try {
            new Account("SB1", "Yomi", new BigDecimal("-1"));
            fail("Expected InvalidAccountException");
        } catch (InvalidAccountException e) {
            assertTrue(e.getMessage().contains("negative"));
        }
    }

    @Test
    public void zeroOpeningBalanceIsAllowed() throws InvalidAccountException {
        Account zero = new Account("SB2", "Ade", BigDecimal.ZERO);
        assertMoney("0", zero.getBalance());
    }

    //  deposits

    @Test
    public void depositIncreasesBalanceAndIsRecorded() throws InvalidAmountException {
        account.deposit(new BigDecimal("5000"));
        assertMoney("205000", account.getBalance());
        assertEquals(1, account.getHistory().length);
        assertEquals(TransactionType.DEPOSIT, account.getHistory()[0].getType());
    }

    @Test
    public void depositOfNegative500IsRejectedAndNothingChanges() {
        try {
            account.deposit(new BigDecimal("-500"));
            fail("Expected InvalidAmountException");
        } catch (InvalidAmountException e) {
            assertMoney("200000", account.getBalance());
            assertEquals(0, account.getHistory().length);
        }
    }

    @Test
    public void depositOfZeroIsRejected() {
        try {
            account.deposit(BigDecimal.ZERO);
            fail("Expected InvalidAmountException");
        } catch (InvalidAmountException e) {
            assertMoney("200000", account.getBalance());
        }
    }

    // withdrawals

    @Test
    public void withdrawalReducesBalanceAndIsRecorded() throws SafeBankException {
        account.withdraw(new BigDecimal("30000"));
        assertMoney("170000", account.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, account.getHistory()[0].getType());
    }

    @Test
    public void withdrawalExceedingBalanceIsRejected() throws InvalidAccountException {
        Account small = new Account("SB3", "Tola", new BigDecimal("3000"));
        try {
            small.withdraw(new BigDecimal("5000"));
            fail("Expected InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            assertMoney("3000", small.getBalance());
            assertEquals(0, small.getHistory().length);
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void withdrawal120000From200000ExceedsSingleLimit() {
        try {
            account.withdraw(new BigDecimal("120000"));
            fail("Expected WithdrawalLimitExceededException");
        } catch (WithdrawalLimitExceededException e) {
            assertMoney("200000", account.getBalance());
            assertEquals(0, account.getHistory().length);
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void withdrawalOfExactlyTheLimitIsAllowed() throws SafeBankException {
        account.withdraw(new BigDecimal("100000"));
        assertMoney("100000", account.getBalance());
    }

    @Test
    public void withdrawalOfZeroOrNegativeIsRejected() {
        try {
            account.withdraw(new BigDecimal("-10"));
            fail("Expected InvalidAmountException");
        } catch (InvalidAmountException e) {
            assertMoney("200000", account.getBalance());
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }
}