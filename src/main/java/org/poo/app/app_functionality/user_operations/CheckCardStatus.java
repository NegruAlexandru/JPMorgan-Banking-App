package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public class CheckCardStatus extends Operation {
    public CheckCardStatus(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Check card status and freeze it if the account balance is below the minimum
     */
    @Override
    public void execute() {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());
        if (card == null) {
            addMessageToOutput("description", "Card not found");
            return;
        }

        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }

        if (card.getCardStatus().equals("active")
                && account.getBalance() <= account.getMinBalance()) {
            card.setCardStatus("frozen");
            addTransaction("You have reached the minimum amount of funds, the card will be frozen",
                    account);
        }
    }

    public void addTransaction(final String description, final Account account) {
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(handler);
    }
}
