package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.SavingsAccount;
import org.poo.utils.Operation;

public class ChangeInterestRate extends Operation {
    public ChangeInterestRate(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Change the interest rate of a savings account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            return;
        }

        if (account.getType().equals("savings")) {
            SavingsAccount savingsAccount = (SavingsAccount) account;
            savingsAccount.setInterestRate(handler.getInterestRate());

            addTransactionToDB("Interest rate of the account changed to " + handler.getInterestRate());
        } else {
            addTransactionToOutput("description", "This is not a savings account");
        }
    }
}
