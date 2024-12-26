package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;

public abstract class AddFunds {
    /**
     * Add funds to the account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        AccountHandler.addFunds(handler.getAccount(), handler.getAmount());
    }
}
