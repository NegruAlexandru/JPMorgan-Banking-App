package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.utils.Operation;

public class ChangeSpendingLimit extends Operation {
    public ChangeSpendingLimit(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Change the spending limit of a business account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            return;
        }

        if (!account.getType().equals("business")) {
            addTransactionToOutput("description", "This is not a business account");
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;

        // check ownership
        if (!businessAccount.getOwner().getEmail().equals(handler.getEmail())) {
            addTransactionToOutput("description", "You must be owner in order to change spending limit.");
            return;
        }

        businessAccount.setSpendingLimit(handler.getAmount());
    }
}
