package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

public class CreateOneTimeCard extends Operation {
    public CreateOneTimeCard(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Creates a new one-time card for the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        addTransaction("New card created", account.createOneTimeCard(handler.getEmail()));
    }

    /**
     * Adds a transaction to the output
     * @param description the description of the transaction
     * @param cardNumber the card number
     */
    public void addTransaction(final String description, final String cardNumber) {
        handler.setCardNumber(cardNumber);
        handler.setDescription(description);
        TransactionHandler.addTransactionCard(handler);
    }
}
