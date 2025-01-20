package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

public class CreateCard extends Operation {
    public CreateCard(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Creates a new card for the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        addTransaction("New card created", account.createCard(handler.getEmail()));
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
