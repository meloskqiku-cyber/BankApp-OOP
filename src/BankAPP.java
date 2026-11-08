
import java.util.Scanner;
import java.util.Random;

public class BankAPP {
    public static void main(String[] args) {
        BankApplication.run();
    }
}

class Transaction {
    private int reference;
    private String type;
    private double amount;

    private Transaction(int reference, String type, double amount) {
        this.reference = reference;
        this.type = type;
        this.amount = amount;
    }

    public static Transaction getInstance(int reference, String type, double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            return null;
        }

        return new Transaction(reference, type, amount);
    }


    public int getReference() {
        return this.reference;
    }

    public String getType() {
        return this.type;
    }

    public double getAmount() {
        return this.amount;
    }

    public void displayDetails() {
        String sign = "+";

        if (this.type.equals("Terheqje")) {
            sign = "-";
        }

        System.out.printf("Ref: %d | %s | %s%.2f EUR%n",
                this.reference, this.type, sign, this.amount);
    }
}

class BankAccount {
    private String owner;
    private String accountNumber;
    private double balance;
    private Transaction[] transactions;

    public BankAccount(String owner, String accountNumber) {
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.balance = 0;
        this.transactions = new Transaction[10];
    }


    public String getOwner() {
        return this.owner;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public double getBalance() {
        return this.balance;
    }

    public Transaction[] getTransactions() {
        return this.transactions;
    }


    public void setOwner(String owner) {
        if (owner != null && !owner.trim().isEmpty()) {
            this.owner = owner;
        }
    }

    public void setAccountNumber(String accountNumber) {
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            this.accountNumber = accountNumber;
        }
    }

    public int getTotalTransactions() {
        int count = 0;

        for (Transaction transaction : this.transactions) {
            if (transaction != null) {
                count++;
            }
        }

        return count;
    }

    public void addNewTransaction(Transaction transaction) {
        if (transaction == null) {
            System.out.println("Transaksioni nuk eshte valid!");
            return;
        }

        this.transactions = BankUtils.addTransaction(this, transaction);
    }

    public void deposit(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            System.out.println("Shuma duhet te jete me e madhe se 0!");
            return;
        }

        this.balance += amount;

        int reference = BankUtils.generateReference(this.transactions);

        Transaction transaction = Transaction.getInstance(
                reference, "Depozitim", amount);

        this.addNewTransaction(transaction);

        System.out.println("Depozitimi u krye me sukses!");
        System.out.printf("Bilanci aktual: %.2f EUR%n", this.balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0 || !Double.isFinite(amount)) {
            System.out.println("Shuma duhet te jete me e madhe se 0!");
            return;
        }

        if (amount > this.balance) {
            System.out.println("Bilanc i pamjaftueshem!");
            return;
        }

        this.balance -= amount;

        int reference = BankUtils.generateReference(this.transactions);

        Transaction transaction = Transaction.getInstance(
                reference, "Terheqje", amount);

        this.addNewTransaction(transaction);

        System.out.println("Terheqja u krye me sukses!");
        System.out.printf("Bilanci aktual: %.2f EUR%n", this.balance);
    }

    public void displayDetails() {
        System.out.println("\n======= ACCOUNT DETAILS =======");
        System.out.println("Pronari: " + this.owner);
        System.out.println("Numri i llogarise: " + this.accountNumber);
        System.out.printf("Bilanci: %.2f EUR%n", this.balance);
        System.out.println("Total transaksione: " + this.getTotalTransactions());
    }

    public void displayHistory() {
        System.out.println("\n======= TRANSACTION HISTORY =======");

        if (this.getTotalTransactions() == 0) {
            System.out.println("Nuk ka transaksione!");
        }

        for (Transaction transaction : this.transactions) {
            if (transaction != null) {
                transaction.displayDetails();
            }
        }

        System.out.println("-----------------------------------");
        System.out.printf("Bilanci aktual: %.2f EUR%n", this.balance);
        System.out.println("Total transaksione: " + this.getTotalTransactions());
    }
}

class BankUtils {

    public static Transaction[] addTransaction(
            BankAccount account, Transaction transaction) {

        Transaction[] transactions = account.getTransactions();

        int index = BankUtils.getNextAvailableIndex(transactions);

        if (index == -1) {
            transactions = BankUtils.extendTransactionArray(transactions);
            index = BankUtils.getNextAvailableIndex(transactions);
        }

        transactions[index] = transaction;

        return transactions;
    }

    public static int getNextAvailableIndex(Transaction[] transactions) {
        for (int i = 0; i < transactions.length; i++) {
            if (transactions[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public static Transaction[] extendTransactionArray(Transaction[] transactions) {
        Transaction[] newArray = new Transaction[transactions.length + 10];

        for (int i = 0; i < transactions.length; i++) {
            newArray[i] = transactions[i];
        }

        return newArray;
    }

    public static int generateReference(Transaction[] transactions) {
        Random random = new Random();
        int reference;
        boolean exists;

        do {
            reference = random.nextInt(900000) + 100000;
            exists = false;

            for (Transaction transaction : transactions) {
                if (transaction != null &&
                        transaction.getReference() == reference) {
                    exists = true;
                }
            }

        } while (exists);

        return reference;
    }
}

class BankApplication {

    private static BankAccount account = null;

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            BankApplication.displayMenu();

            int userInput;

            if (scanner.hasNextInt()) {
                userInput = scanner.nextInt();
            } else {
                scanner.nextLine();
                System.out.println("Opsion i pavlefshem!");
                continue;
            }

            switch (userInput) {
                case 1:
                    BankApplication.createOrUpdateAccount(scanner);
                    break;
                case 2:
                    BankApplication.depositMoney(scanner);
                    break;
                case 3:
                    BankApplication.withdrawMoney(scanner);
                    break;
                case 4:
                    BankApplication.displayAccountData();
                    break;
                case 5:
                    BankApplication.displayTransactionHistory();
                    break;
                case 0:
                    running = false;
                    System.out.println("Faleminderit qe perdoret BankApp!");
                    break;
                default:
                    System.out.println("Opsion i pavlefshem!");
            }
        }

        scanner.close();
    }

    private static void createOrUpdateAccount(Scanner scanner) {
        scanner.nextLine();

        if (BankApplication.account == null) {
            System.out.println("Krijimi i llogarise bankare");
        } else {
            System.out.println("Perditesimi i llogarise bankare");
        }

        System.out.print("Emri dhe mbiemri: ");
        String owner = scanner.nextLine();

        System.out.print("Numri i llogarise: ");
        String accountNumber = scanner.nextLine();

        if (owner.trim().isEmpty() || accountNumber.trim().isEmpty()) {
            System.out.println("Te dhenat nuk mund te jene te zbrazeta!");
            return;
        }

        if (BankApplication.account == null) {
            BankApplication.account = new BankAccount(owner, accountNumber);
        } else {
            BankApplication.account.setOwner(owner);
            BankApplication.account.setAccountNumber(accountNumber);
        }

        System.out.println("Te dhenat u ruajten me sukses!");
    }

    private static void depositMoney(Scanner scanner) {
        if (BankApplication.account == null) {
            System.out.println("Duhet te krijoni nje llogari fillimisht!");
            return;
        }

        System.out.print("Shuma per depozitim: ");

        if (!scanner.hasNextDouble()) {
            scanner.nextLine();
            System.out.println("Shuma nuk eshte valide!");
            return;
        }

        double amount = scanner.nextDouble();

        BankApplication.account.deposit(amount);
    }

    private static void withdrawMoney(Scanner scanner) {
        if (BankApplication.account == null) {
            System.out.println("Duhet te krijoni nje llogari fillimisht!");
            return;
        }

        System.out.print("Shuma per terheqje: ");

        if (!scanner.hasNextDouble()) {
            scanner.nextLine();
            System.out.println("Shuma nuk eshte valide!");
            return;
        }

        double amount = scanner.nextDouble();

        BankApplication.account.withdraw(amount);
    }

    private static void displayAccountData() {
        if (BankApplication.account == null) {
            System.out.println("Duhet te krijoni nje llogari fillimisht!");
            return;
        }

        BankApplication.account.displayDetails();
    }

    private static void displayTransactionHistory() {
        if (BankApplication.account == null) {
            System.out.println("Duhet te krijoni nje llogari fillimisht!");
            return;
        }

        BankApplication.account.displayHistory();
    }

    private static void displayMenu() {
        System.out.println("\n======= BANK APP =======");
        System.out.println("1. Perditeso te dhenat e llogarise");
        System.out.println("2. Depozito para");
        System.out.println("3. Terhiq para");
        System.out.println("4. Shfaq detajet e llogarise");
        System.out.println("5. Shfaq historikun e transaksioneve");
        System.out.println("0. Dil nga aplikacioni");
        System.out.print("Zgjedh opsionin: ");
    }
}
