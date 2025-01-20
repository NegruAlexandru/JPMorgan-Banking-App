package org.poo.app.appFunctionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.baseClasses.Transaction;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.baseClasses.User;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

import java.util.ArrayList;
import java.util.Comparator;

import static org.poo.app.logicHandlers.CommandHandler.OBJECT_MAPPER;

public class PrintTransactions extends Operation {
    public PrintTransactions(final CommandHandler handler,
                             final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Get all transactions for a user
     */
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());

        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        ArrayNode outputNode = OBJECT_MAPPER.createArrayNode();
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (Account account : user.getAccounts()) {
            transactions.addAll(account.getTransactions());
        }

        transactions.sort(Comparator.comparingDouble(Transaction::getTimestamp));

        for (Transaction t : transactions) {
            outputNode.add(Transaction.formatOutput(t));
        }

        addOutputNodeToOutput(node, outputNode);
    }

    private void addOutputNodeToOutput(final ObjectNode node,
                                       final ArrayNode outputNode) {
        node.put("command", handler.getCommand());
        node.set("output", outputNode);
        node.put("timestamp", handler.getTimestamp());
        output.add(node);
    }
}
