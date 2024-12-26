package org.poo.app.app_functionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;

import static org.poo.app.logic_handlers.CommandHandler.ACCOUNT_HANDLER;
import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class PrintUsers {
    /**
     * Get all users
     * @return output of the command
     */
    public static ArrayNode execute() {
        ArrayNode output = OBJECT_MAPPER.createArrayNode();
        for (User u : DB.getUsers().values()) {
            output.add(ACCOUNT_HANDLER.visit(u));
        }

        return output;
    }
}
