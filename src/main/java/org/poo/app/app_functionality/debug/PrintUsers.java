package org.poo.app.app_functionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public class PrintUsers extends Operation {
    private static final AccountHandler ACCOUNT_HANDLER = new AccountHandler();

    public PrintUsers(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Get all users
     */
    public void execute() {
        ArrayNode node = OBJECT_MAPPER.createArrayNode();
        for (User u : DB.getUsers().values()) {
            node.add(ACCOUNT_HANDLER.visit(u));
        }

        output.add(node);
    }
}
