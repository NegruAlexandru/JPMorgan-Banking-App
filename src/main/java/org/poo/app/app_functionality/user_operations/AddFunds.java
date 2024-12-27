package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.utils.Operation;

public class AddFunds extends Operation {
    public AddFunds(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Add funds to the account
     */
    @Override
    public void execute() {
        AccountHandler.addFunds(handler.getAccount(), handler.getAmount());
    }
}
