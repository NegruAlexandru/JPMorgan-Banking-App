package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.payment.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.SavingsAccount;
import org.poo.utils.Operation;

public class AddInterest extends Operation {
    public AddInterest(final CommandHandler handler,
                       final ArrayNode output) {
        super(handler, output);
    }
    /**
     * Add interest to a savings account
     *
     */
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            return;
        }

        if (!account.getType().equals("savings")) {
            addTransactionToOutput("description", "This is not a savings account");
            return;
        }

        double amount = account.getBalance() * ((SavingsAccount) account).getInterestRate();

        AccountHandler.addFunds(account, amount);

        addTransactionToDB(account, amount);
    }

    /**
     * Add a transaction to the database
     *
     * @param account the account
     * @param amount  the amount of the transaction
     */
    private void addTransactionToDB(final Account account, final double amount) {
        handler.setDescription("Interest rate income");
        handler.setCurrency(account.getCurrency());
        handler.setAmount(amount);
        TransactionHandler.addInterestTransactionToDB(handler);
    }
}
