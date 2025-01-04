package org.poo.app.user_facilities;

import lombok.Getter;
import lombok.Setter;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.DB;

import java.util.ArrayList;

@Getter
@Setter
public class BusinessAccount extends Account {
    private User owner;
    private ArrayList<User> managers;
    private ArrayList<User> employees;
    private double spendingLimit;
    private double depositLimit;
    private ArrayList<Transaction> businessTransactions;

    public BusinessAccount(final String email, final String currency) {
        super(email, currency);
        this.setType("business");
        this.owner = DB.findUserByEmail(email);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
    }
}
