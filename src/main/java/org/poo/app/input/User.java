package org.poo.app.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.SavingsAccount;
import org.poo.fileio.UserInput;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@NoArgsConstructor
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;
    private HashMap<String, String> aliases;

    public User(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.aliases = new HashMap<>();
    }

    public User(final UserInput user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.aliases = new HashMap<>();
    }

    /**
     * Adds a new account to the user
     * @param currency currency of the account
     * @return the new account
     */
    public Account addAccount(final String currency) {
        Account account = new Account(email, currency);
        this.accounts.add(account);
        DB.addAccount(account);

        return account;
    }

    /**
     * Adds a new savings account to the user
     * @param currency currency of the account
     * @param interestRate interest rate of the account
     * @return the new account
     */
    public Account addSavingsAccount(final String currency, final double interestRate) {
        Account account = new SavingsAccount(email, currency, interestRate);
        this.accounts.add(account);
        DB.addAccount(account);

        return account;
    }

    /**
     * Deletes an account from the user
     * @param account account to be deleted
     * @return the deleted account
     */
    public Account deleteAccount(final Account account) {
        accounts.remove(account);
        DB.removeAccount(account);
        account.deleteAllCards();

        return account;
    }
}
