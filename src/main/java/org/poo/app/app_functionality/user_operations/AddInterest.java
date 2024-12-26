package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.SavingsAccount;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class AddInterest {
    /**
     * Add interest to a savings account
     * @param handler current CommandHandler object
     * @return error ObjectNode if the account is not a savings account
     *        or null if the operation was successful
     */
    public static ObjectNode execute(final CommandHandler handler) {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            return null;
        }

        if (!account.getType().equals("savings")) {
            ObjectNode error = OBJECT_MAPPER.createObjectNode();
            error.put("description", "This is not a savings account");
            error.put("timestamp", handler.getTimestamp());
            return error;
        }

        AccountHandler.addFunds(account, account.getBalance()
                * ((SavingsAccount) account).getInterestRate());

        return null;
    }
}
