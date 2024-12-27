package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

public class SetMinimumBalance extends Operation {
    public SetMinimumBalance(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Set the minimum balance for the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        account.setMinBalance(handler.getAmount());
    }
}
