package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.input.Transaction;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

import java.util.TreeMap;

import static org.poo.app.logicHandlers.CommandHandler.OBJECT_MAPPER;
import static org.poo.app.input.Transaction.formatOutput;

public class SpendingsReport extends Operation {
    public SpendingsReport(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Create a spending report for an account
     */
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            addTransactionToOutput("description", "Account not found");
            return;
        }

        if (account.getType().equals("savings")) {
            addOutputMessageToOutput("error",
                "This kind of report is not supported for a saving account");
            return;
        }

        ArrayNode commerciantTransactions = OBJECT_MAPPER.createArrayNode();
        TreeMap<String, Double> spending = new TreeMap<>();

        calculateCommerciantTransactionsAndSpending(account,
                handler, commerciantTransactions, spending);

        ArrayNode commerciants = OBJECT_MAPPER.createArrayNode();
        for (String key : spending.keySet()) {
            ObjectNode commerciant = OBJECT_MAPPER.createObjectNode();
            commerciant.put("commerciant", key);
            commerciant.put("total", spending.get(key));
            commerciants.add(commerciant);
        }

        addMessageToOutput(account, commerciants, commerciantTransactions);
    }

    /**
     * Calculate the transactions with commerciant and the total spending
     * @param account account to calculate the transactions for
     * @param handler current CommandHandler object
     * @param transactionsWithCommerciant ArrayNode to store the transactions
     * @param spending TreeMap to store the total spending
     */
    public void calculateCommerciantTransactionsAndSpending(
            final Account account,
            final CommandHandler handler,
            final ArrayNode transactionsWithCommerciant,
            final TreeMap<String, Double> spending) {
        for (Transaction t : account.getTransactions()) {
            if (t.getCommerciant() != null
                    && t.getTimestamp() >= handler.getStartTimestamp()
                    && t.getTimestamp() <= handler.getEndTimestamp()) {
                transactionsWithCommerciant.add(formatOutput(t));

                if (spending.containsKey(t.getCommerciant())) {
                    spending.put(t.getCommerciant(),
                            spending.get(t.getCommerciant())
                                    + t.getAmountDouble());
                } else {
                    spending.put(t.getCommerciant(), t.getAmountDouble());
                }
            }
        }
    }

    private void addMessageToOutput(final Account account, final ArrayNode commerciants,
                                   final ArrayNode commerciantTransactions) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());
        node.put("timestamp", handler.getTimestamp());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());
        outputNode.put("IBAN", account.getIban());
        outputNode.set("commerciants", commerciants);
        outputNode.set("transactions", commerciantTransactions);

        node.set("output", outputNode);
        output.add(node);
    }

    private void addOutputMessageToOutput(String key, String value) {
        ObjectNode node = OBJECT_MAPPER.createObjectNode();
        node.put("command", handler.getCommand());
        node.set("output", OBJECT_MAPPER.createObjectNode().put(key, value));
        node.put("timestamp", handler.getTimestamp());
        output.add(node);
    }
}
