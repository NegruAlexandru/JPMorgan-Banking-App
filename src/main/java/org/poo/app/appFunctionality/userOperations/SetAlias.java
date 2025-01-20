package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.input.User;
import org.poo.utils.Operation;

public class SetAlias extends Operation {
    public SetAlias(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Set an alias for the account
     */
    @Override
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());

        user.getAliases().put(handler.getAlias(), handler.getAccount());
    }
}
