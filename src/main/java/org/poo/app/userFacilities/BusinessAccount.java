package org.poo.app.userFacilities;

import lombok.Getter;
import lombok.Setter;
import org.poo.app.baseClasses.Transaction;
import org.poo.app.baseClasses.User;
import org.poo.app.logicHandlers.DB;

import java.util.ArrayList;

@Getter
@Setter
public class BusinessAccount extends Account {
    private ArrayList<User> managers;
    private ArrayList<User> employees;
    private double spendingLimit;
    private double depositLimit;
    private ArrayList<Transaction> businessTransactions;
    private static final int INITIAL_SPENDING_LIMIT = 500;
    private static final int INITIAL_DEPOSIT_LIMIT = 500;

    public BusinessAccount(final String email,
                           final String currency) {
        super(email, currency);
        this.setType("business");
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.spendingLimit = DB.convert(INITIAL_SPENDING_LIMIT, "RON", currency);
        this.depositLimit = DB.convert(INITIAL_DEPOSIT_LIMIT, "RON", currency);
        this.businessTransactions = new ArrayList<>();
    }

    /**
     * Searches for the owner's user of the account and returns it
     * @return the owner of the account
     */
    public User getOwner() {
        return DB.findUserByEmail(getEmail());
    }
}
