package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;

public abstract class DeleteCard {
    /**
     * Deletes a card from the account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }

        handler.setEmail(account.getEmail());
        handler.setDescription("The card has been destroyed");
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(handler);
        account.deleteCard(handler.getCardNumber());
    }
}
