package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

public abstract class AddAccount {
    /**
     * Adds a new account to the user
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        User user = DB.findUserByEmail(handler.getEmail());

        Account newAccount;
        if (handler.getAccountType().equals("savings")) {
            newAccount = user.addSavingsAccount(handler.getCurrency(), handler.getInterestRate());
        } else {
            newAccount = user.addAccount(handler.getCurrency());
        }

        handler.setAccount(newAccount.getIban());
        handler.setDescription("New account created");
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }
}
