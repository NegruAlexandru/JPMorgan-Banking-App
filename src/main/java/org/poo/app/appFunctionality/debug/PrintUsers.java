package org.poo.app.appFunctionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logicHandlers.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.input.User;
import org.poo.utils.Operation;

import static org.poo.app.logicHandlers.CommandHandler.OBJECT_MAPPER;

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

        addOutputNodeToOutput(node);
    }

    private void addOutputNodeToOutput(final ArrayNode outputNode) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());
        node.put("timestamp", handler.getTimestamp());
        node.set("output", outputNode);
        output.add(node);
    }
}
