package org.poo.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.TransactionHandler;

import static org.poo.app.logicHandlers.CommandHandler.OBJECT_MAPPER;

public abstract class Operation {
    protected final CommandHandler handler;
    protected final ArrayNode output;

    public Operation(final CommandHandler handler, final ArrayNode output) {
        this.handler = handler;
        this.output = output;
    }

    public abstract void execute();

    public void addTransactionToOutput(String key, String value) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put(key, value);
        outputNode.put("timestamp", handler.getTimestamp());
        node.set("output", outputNode);

        node.put("timestamp", handler.getTimestamp());
        output.add(node);
    }

    public void addTransactionToDB(String description) {
        handler.setDescription(description);
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }
}
