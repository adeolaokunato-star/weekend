package ng.safebank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class SafeBankApp {

    private final Bank bank;
    private final Scanner scanner;

    public SafeBankApp(Scanner scanner) {
        this.bank = new Bank();
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        SafeBankApp app = new SafeBankApp(scanner);
        app.run();
        scanner.close();
    }

    public void run() {
        System.out.println("=====================================");
        System.out.println("   Welcome to SafeBank Simulator");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ");
            if (choice == null) {
                // input ended (for example piped input finished)
                System.out.println("\nNo more input. Goodbye!");
                running = false;
            } else {
                running = handleChoice(choice.trim());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------- MAIN MENU ----------");
        System.out.println("1. Create account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check balance");
        System.out.println("5. Transaction history");
        System.out.println("6. Transfer between accounts");
        System.out.println("7. List all accounts");
        System.out.println("0. Exit");
    }

    private boolean handleChoice(String choice) {
        switch (choice) {
            case "1":
                createAccount();
                break;
            case "2":
                deposit();
                break;
            case "3":
                withdraw();
                break;
            case "4":
                checkBalance();
                break;
            case "5":
                showHistory();
                break;
            case "6":
                transfer();
                break;
            case "7":
                listAccounts();
                break;
            case "0":
                System.out.println("Thank you for using SafeBank. Goodbye!");
                return false;
            default:
                System.out.println("Invalid option '" + choice + "'. Please type a number from the menu (0 to 7).");
                break;
        }
        return true;
    }

    private void createAccount() {
        String name = readLine("Account holder's name: ");
        String openingText = readLine("Opening balance (\u20A6): ");
        try {
            BigDecimal opening = AmountParser.parse(openingText);
            Account account = bank.createAccount(name, opening);
            System.out.println("Account created. Account number: " + account.getAccountNumber()
                    + " | Holder: " + account.getHolderName()
                    + " | Balance: " + Money.format(account.getBalance()));
        } catch (SafeBankException e) {
            System.out.println("Could not create account: " + e.getMessage());
        }
    }

    private void deposit() {
        String number = readLine("Account number: ");
        String amountText = readLine("Amount to deposit (\u20A6): ");
        try {
            Account account = bank.findAccount(number);
            BigDecimal amount = AmountParser.parse(amountText);
            account.deposit(amount);
            System.out.println("Deposit successful. New balance: " + Money.format(account.getBalance()));
        } catch (SafeBankException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private void withdraw() {
        String number = readLine("Account number: ");
        String amountText = readLine("Amount to withdraw (\u20A6): ");
        try {
            Account account = bank.findAccount(number);
            BigDecimal amount = AmountParser.parse(amountText);
            account.withdraw(amount);
            System.out.println("Withdrawal successful. New balance: " + Money.format(account.getBalance()));
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (WithdrawalLimitExceededException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        } catch (SafeBankException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private void checkBalance() {
        String number = readLine("Account number: ");
        try {
            Account account = bank.findAccount(number);
            System.out.println(account.getHolderName() + " (" + account.getAccountNumber() + ") balance: "
                    + Money.format(account.getBalance()));
        } catch (SafeBankException e) {
            System.out.println("Could not check balance: " + e.getMessage());
        }
    }

    private void showHistory() {
        String number = readLine("Account number: ");
        try {
            Account account = bank.findAccount(number);
            Transaction[] history = account.getHistory();
            if (history.length == 0) {
                System.out.println("No successful transactions yet for " + account.getAccountNumber() + ".");
            } else {
                System.out.println("Successful transactions for " + account.getAccountNumber() + ":");
                for (int i = 0; i < history.length; i++) {
                    System.out.println((i + 1) + ". " + history[i]);
                }
            }
        } catch (SafeBankException e) {
            System.out.println("Could not show history: " + e.getMessage());
        }
    }

    private void transfer() {
        String fromNumber = readLine("Transfer FROM account number: ");
        String toNumber = readLine("Transfer TO account number: ");
        String amountText = readLine("Amount to transfer (\u20A6): ");
        try {
            BigDecimal amount = AmountParser.parse(amountText);
            bank.transfer(fromNumber, toNumber, amount);
            System.out.println("Transfer of " + Money.format(amount) + " successful.");
        } catch (SafeBankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private void listAccounts() {
        List<Account> accounts = bank.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet. Choose option 1 to create one.");
        } else {
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                System.out.println(account.getAccountNumber() + " | " + account.getHolderName()
                        + " | " + Money.format(account.getBalance()));
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }
}