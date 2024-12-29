package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;
import static org.poo.app.app_functionality.debug.Transaction.formatOutput;

public class Report extends Operation {
    public Report(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Create a report of all transactions for an account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            addTransactionToOutput("description", "Account not found");
            return;
        }

        ArrayNode transactions = OBJECT_MAPPER.createArrayNode();
        for (Transaction t : account.getTransactions()) {
            if (t.getTimestamp() >= handler.getStartTimestamp()
                    && t.getTimestamp() <= handler.getEndTimestamp()) {
                transactions.add(formatOutput(t));
            }
        }
        this.addMessageToOutput(transactions, account);
    }

    public void addMessageToOutput(final ArrayNode transactions, final Account account) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());
        node.put("timestamp", handler.getTimestamp());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("IBAN", account.getIban());
        outputNode.set("transactions", transactions);

        node.set("output", outputNode);
        output.add(node);
    }
}
