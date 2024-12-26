package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;

public abstract class SetAlias {
    /**
     * Set an alias for the account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        User user = DB.findUserByEmail(handler.getEmail());

        user.getAliases().put(handler.getAlias(), handler.getAccount());
    }
}
