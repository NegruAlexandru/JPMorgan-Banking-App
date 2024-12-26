package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.PaymentHandler;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class PayOnline {
    /**
     * Pay online with a card
     * @param handler current CommandHandler object
     * @return error ObjectNode if the card is frozen
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

        Account ownerAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (ownerAccount == null) {
            return null;
        }

        if (card.getCardStatus().equals("frozen")) {
            handler.setDescription("The card is frozen");
            handler.setAccount(ownerAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return null;
        }

        handler.setAmount(DB.convert(handler.getAmount(), handler.getCurrency(),
                ownerAccount.getCurrency()));

        PaymentHandler.pay(ownerAccount, card, handler);
        return null;
    }
}
