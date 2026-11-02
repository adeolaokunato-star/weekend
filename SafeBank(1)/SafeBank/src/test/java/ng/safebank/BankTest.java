package ng.safebank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTest {

    private Bank bank;
    private Account yomi;
    private Account ade;

    @BeforeEach
    public void setUp() throws InvalidAccountException {
        bank = new Bank();
        yomi = bank.createAccount("Yomi", new BigDecimal("200000"));
        ade = bank.createAccount("Ade", new BigDecimal("1000"));
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    public void createAccountWithBlankNameFailsAndDoesNotAddAccount() {
        int before = bank.getAllAccounts().size();
        try {
            bank.createAccount("", new BigDecimal("100"));
            fail("Expected InvalidAccountException");
        } catch (InvalidAccountException e) {
            assertEquals(before, bank.getAllAccounts().size());
        }
    }

    @Test
    public void findingUnknownAccountFails() {
        try {
            bank.findAccount("SB9999");
            fail("Expected AccountNotFoundException");
        } catch (AccountNotFoundException e) {
            assertEquals(2, bank.getAllAccounts().size());
        }
    }

    @Test
    public void successfulTransferMovesMoneyAndRecordsBothSides() throws SafeBankException {
        bank.transfer(yomi.getAccountNumber(), ade.getAccountNumber(), new BigDecimal("40000"));
        assertMoney("160000", yomi.getBalance());
        assertMoney("41000", ade.getBalance());
        assertEquals(TransactionType.TRANSFER_OUT, yomi.getHistory()[0].getType());
        assertEquals(TransactionType.TRANSFER_IN, ade.getHistory()[0].getType());
    }

    @Test
    public void transferWithInsufficientFundsChangesNeitherAccount() {
        try {
            bank.transfer(ade.getAccountNumber(), yomi.getAccountNumber(), new BigDecimal("5000"));
            fail("Expected InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            assertMoney("1000", ade.getBalance());
            assertMoney("200000", yomi.getBalance());
            assertEquals(0, ade.getHistory().length);
            assertEquals(0, yomi.getHistory().length);
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void transferOverWithdrawalLimitChangesNeitherAccount() {
        try {
            bank.transfer(yomi.getAccountNumber(), ade.getAccountNumber(), new BigDecimal("120000"));
            fail("Expected WithdrawalLimitExceededException");
        } catch (WithdrawalLimitExceededException e) {
            assertMoney("200000", yomi.getBalance());
            assertMoney("1000", ade.getBalance());
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void transferToSameAccountIsRejected() {
        try {
            bank.transfer(yomi.getAccountNumber(), yomi.getAccountNumber(), new BigDecimal("100"));
            fail("Expected InvalidAccountException");
        } catch (InvalidAccountException e) {
            assertMoney("200000", yomi.getBalance());
            assertEquals(0, yomi.getHistory().length);
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void transferToUnknownAccountChangesNothing() {
        try {
            bank.transfer(yomi.getAccountNumber(), "SB9999", new BigDecimal("100"));
            fail("Expected AccountNotFoundException");
        } catch (AccountNotFoundException e) {
            assertMoney("200000", yomi.getBalance());
        } catch (SafeBankException e) {
            fail("Wrong exception type: " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void transferWorksImmediatelyAfterFailedTransfer() throws SafeBankException {
        try {
            bank.transfer(yomi.getAccountNumber(), ade.getAccountNumber(), new BigDecimal("-5"));
        } catch (InvalidAmountException e) {
            assertMoney("200000", yomi.getBalance());
        }
        bank.transfer(yomi.getAccountNumber(), ade.getAccountNumber(), new BigDecimal("1000"));
        assertMoney("199000", yomi.getBalance());
        assertMoney("2000", ade.getBalance());
    }
}
