package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

public class DeleteCard extends Operation {
    public DeleteCard(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Deletes a card from the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }

        addTransaction("The card has been destroyed", account);
        account.deleteCard(handler.getCardNumber());
    }

    public void addTransaction(final String description, final Account account) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(handler);
    }
}
