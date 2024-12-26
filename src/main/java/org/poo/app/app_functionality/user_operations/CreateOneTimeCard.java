package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;

public abstract class CreateOneTimeCard {
    /**
     * Creates a new one-time card for the account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        handler.setCardNumber(account.createOneTimeCard());
        handler.setDescription("New card created");
        TransactionHandler.addTransactionCard(handler);
    }
}
