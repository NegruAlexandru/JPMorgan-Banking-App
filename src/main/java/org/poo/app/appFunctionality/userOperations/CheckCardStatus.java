package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Card;
import org.poo.utils.Operation;

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
            addTransactionToOutput("description", "Card not found");
            return;
        }

        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }
        handler.setAccount(account.getIban());

        if (card.getCardStatus().equals("active")
                && account.getBalance() <= account.getMinBalance()) {
            card.setCardStatus("frozen");
            addTransactionToDB("You have reached the minimum amount of funds, the card will be frozen");
        } else if (card.getCardStatus().equals("frozen")
                && account.getBalance() > account.getMinBalance()) {
            card.setCardStatus("active");
            addTransactionToDB("The card has been unfrozen");
        }
    }
}
