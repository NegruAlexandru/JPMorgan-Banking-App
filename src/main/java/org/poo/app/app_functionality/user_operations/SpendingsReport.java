package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.user_facilities.Account;

import java.util.TreeMap;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;
import static org.poo.app.app_functionality.debug.Transaction.formatOutput;

public abstract class SpendingsReport {
    /**
     * Create a spending report for an account
     * @param handler current CommandHandler object
     * @return the report ObjectNode if the operation was successful
     *       or error ObjectNode if the account is not found
     */
    public static ObjectNode execute(final CommandHandler handler) {
        ObjectNode output = OBJECT_MAPPER.createObjectNode();

        Account account = DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            output.put("description", "Account not found");
            output.put("timestamp", handler.getTimestamp());
            return output;
        }

        if (account.getType().equals("savings")) {
            ObjectNode error = OBJECT_MAPPER.createObjectNode();
            error.put("error",
                "This kind of report is not supported for a saving account");
            return error;
        }

        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("IBAN", account.getIban());

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

        output.set("commerciants", commerciants);
        output.set("transactions", commerciantTransactions);

        return output;
    }

    /**
     * Calculate the transactions with commerciant and the total spending
     * @param account account to calculate the transactions for
     * @param handler current CommandHandler object
     * @param transactionsWithCommerciant ArrayNode to store the transactions
     * @param spending TreeMap to store the total spending
     */
    public static void calculateCommerciantTransactionsAndSpending(
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
}
