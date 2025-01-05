package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.SavingsAccount;
import org.poo.utils.Operation;

public class AddInterest extends Operation {
    public AddInterest(CommandHandler handler, ArrayNode output) {
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

        addTransactionToDB("Interest rate income", account, amount);
    }

    public void addTransactionToDB(String description, Account account, double amount) {
        handler.setDescription(description);
        handler.setCurrency(account.getCurrency());
        handler.setAmount(amount);
        TransactionHandler.addInterestTransactionToDB(handler);
    }
}
