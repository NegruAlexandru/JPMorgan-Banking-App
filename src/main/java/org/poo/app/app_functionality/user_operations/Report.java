package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.user_facilities.Account;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;
import static org.poo.app.app_functionality.debug.Transaction.formatOutput;

public abstract class Report {
    /**
     * Create a report of all transactions for an account
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

        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("IBAN", account.getIban());

        ArrayNode transactions = OBJECT_MAPPER.createArrayNode();
        for (Transaction t : account.getTransactions()) {
            if (t.getTimestamp() >= handler.getStartTimestamp()
                    && t.getTimestamp() <= handler.getEndTimestamp()) {
                transactions.add(formatOutput(t));
            }
        }
        output.set("transactions", transactions);

        return output;
    }
}
