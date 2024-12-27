package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
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
            addMessageToOutput("description", "This is not a savings account");
        }

        AccountHandler.addFunds(account, account.getBalance()
                * ((SavingsAccount) account).getInterestRate());
    }
}
