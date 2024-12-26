package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class CheckCardStatus {
    /**
     * Check card status and freeze it if the account balance is below the minimum
     * @param handler current CommandHandler object
     * @return error ObjectNode if the account is not a savings account
     *        or null if the operation was successful
     */
    public static ObjectNode execute(final CommandHandler handler) {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());
        if (card == null) {
            ObjectNode error = OBJECT_MAPPER.createObjectNode();
            error.put("description", "Card not found");
            error.put("timestamp", handler.getTimestamp());
            return error;
        }

        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return null;
        }

        if (card.getCardStatus().equals("active")
                && account.getBalance() <= account.getMinBalance()) {
            card.setCardStatus("frozen");
            handler.setDescription(
                    "You have reached the minimum amount of funds, the card will be frozen");
            handler.setAccount(account.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
        }

        return null;
    }
}
