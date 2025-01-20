package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.SavingsAccount;
import org.poo.utils.Operation;

public class ChangeInterestRate extends Operation {
    public ChangeInterestRate(final CommandHandler handler,
                              final ArrayNode output) {
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

            addTransactionToDB("Interest rate of the account changed to "
                    + handler.getInterestRate());
        } else {
            addTransactionToOutput("description", "This is not a savings account");
        }
    }
}
