package org.poo.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.TransactionHandler;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class Operation {
    protected final CommandHandler handler;
    protected final ArrayNode output;

    public Operation(final CommandHandler handler, final ArrayNode output) {
        this.handler = handler;
        this.output = output;
    }

    public abstract void execute();
    public void addMessageToOutput(String key, String value) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());
        node.set("output", OBJECT_MAPPER.createObjectNode().put(key, value));
        node.put("timestamp", handler.getTimestamp());
        output.add(node);
    }

    public void addTransaction(String description) {
        handler.setDescription(description);
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }
}
