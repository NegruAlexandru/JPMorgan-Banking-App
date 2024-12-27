package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.PaymentHandler;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public class PayOnline extends Operation {
    public PayOnline(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }
    /**
     * Pay online with a card
     */
    @Override
    public void execute() {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());

        if (card == null) {
            addMessageToOutput("description", "Card not found");
        }

        Account ownerAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (ownerAccount == null) {
            return;
        }

        if (card.getCardStatus().equals("frozen")) {
            handler.setDescription("The card is frozen");
            handler.setAccount(ownerAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        handler.setAmount(DB.convert(handler.getAmount(), handler.getCurrency(),
                ownerAccount.getCurrency()));

        PaymentHandler.pay(ownerAccount, card, handler);
    }
}
