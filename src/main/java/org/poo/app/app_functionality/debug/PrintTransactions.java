package org.poo.app.app_functionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

import java.util.ArrayList;
import java.util.Comparator;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class PrintTransactions {
    /**
     * Get all transactions for a user
     * @param handler current CommandHandler object
     * @return output of the command
     */
    public static ArrayNode execute(final CommandHandler handler) {
        User user = DB.findUserByEmail(handler.getEmail());

        ArrayNode output = OBJECT_MAPPER.createArrayNode();
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (Account account : user.getAccounts()) {
            transactions.addAll(account.getTransactions());
        }

        transactions.sort(Comparator.comparingDouble(Transaction::getTimestamp));

        for (Transaction t : transactions) {
            output.add(Transaction.formatOutput(t));
        }

        return output;
    }
}
