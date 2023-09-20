/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package supportbank;

import supportbank.csv.CsvEntry;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class App {
    private static final Hashtable<String, Account> accounts = new Hashtable<>();


    public static void main(String[] args) {
        // Read the file
        List<String> fileData = Reader.readFileToList(new File("/home/mfuller/Desktop/Bootcamp/week1/SupportBank-Java/Transactions2014.csv"));
        List<CsvEntry> entries = CsvEntry.createEntries(fileData);
        BigDecimal pot = new BigDecimal(0);

        for (CsvEntry entry : entries) {
            // If the user doesn't have an account yet
            if (!accounts.containsKey(entry.getFrom())) {
                // Create account for everyone in from
                createAccount(entry.getFrom());
            }

            // If the owed doesn't have an account yet
            if (!accounts.containsKey(entry.getTo())) {
                // Create account for everyone in to
                createAccount(entry.getTo());
            }

            // Modify the balance (add x to from, subtract x from to)
            accounts.get(entry.getFrom()).addToBalance(entry.getAmount());
            accounts.get(entry.getTo()).subtractFromBalance(entry.getAmount());

            pot = pot.add(entry.getAmount());
        }

        System.out.println("POT: " + pot);
        System.out.println("DIFF: " + findDifference(accounts));

        handleCommands(accounts);
    }

    /**
     * Creates a new account under the given name
     * @param name The name to create the account under
     */
    private static void createAccount(String name) {
        accounts.put(name, new Account(name));
    }

    /**
     * Begins listening for, and processing commands
     * @param accounts Hashtable of the accounts for output
     */
    private static void handleCommands(Hashtable<String, Account> accounts) {
        String command;
        String[] splitCommand;

        try (Scanner sc = new Scanner(System.in)) {
            while (!(command = sc.nextLine()).isEmpty()) {
                System.out.println("Enter command:");
                splitCommand = command.split(" ", 2);

                if (splitCommand[0].equals("list")) {
                    if (splitCommand[1].equals("all")) {
                        listAccounts(accounts);
                    } else {
                        listAccount(accounts.get(splitCommand[1]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lists every account in the input Hashtable
     * @param accounts Hashtable&lt;String, Account&gt; mapping names to accounts
     */
    private static void listAccounts(Hashtable<String, Account> accounts) {
        for (Account account : accounts.values()) {
            // List every account to Standard Out
            listAccount(account);
        }
    }

    /**
     * Lists one specific account's information (name, and balance to 2 d.p.)
     * @param account The account to list
     */
    private static void listAccount(Account account) {
        if (account.getBalance().doubleValue() >= 0) {
            System.out.printf(
                    "%s is owed: %s%n",
                    account.getName(),
                    truncateDecimal(account.getBalance().abs(), 2)
            );
        } else {
            System.out.printf(
                    "%s owes: %s%n",
                    account.getName(),
                    truncateDecimal(account.getBalance().abs(), 2)
            );
        }
    }

    /**
     * <pre>
     *  <strong>
     *      This method assumes the value has numbers after the floating point
     *  </strong>
     * </pre>
     * @param value The value to be truncated
     * @param places The number of decimal places
     * @return A string representation of the number, truncated to have two decimal places
     */
    private static String truncateDecimal(BigDecimal value, int places) {
        // Splits on the floating point
        String[] splitted = value.toString().split("\\.");
        String truncated = "";

        // Everything before the decimal
        truncated += splitted[0];
        // Reinsert decimal
        truncated += ".";
        // Everything after the decimal, to the right amount of places
        truncated += splitted[1].substring(0, places);

        return truncated;
    }

    /**
     * <pre>
     * Finds the difference between all of the accounts, this method should always return zero
     * </pre>
     * @param accounts Hashtable of the accounts to add up
     * @return The BigDecimal generated by adding the balance of every Account in accounts
     */
    private static BigDecimal findDifference(Hashtable<String, Account> accounts) {
        BigDecimal total = new BigDecimal(0);

        for (Account account : accounts.values()) {
            total = total.add(account.getBalance());
        }

        return total;
    }
}
