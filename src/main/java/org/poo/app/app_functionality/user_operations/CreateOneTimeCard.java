package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

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

        String cardNumber = account.createOneTimeCard(handler.getEmail());
        addTransaction("New card created", cardNumber);
//        if (account.getIban().equals(ibannenorocit)) {
//            System.out.println("New one-time card created: " + cardNumber);
//        }
//        System.out.println("New one-time card created: " + cardNumber);
//        System.out.println("Account IBAN: " + account.getIban());
//        System.out.println("Timestamp: " + handler.getTimestamp());
    }

    public void addTransaction(final String description, final String cardNumber) {
        handler.setCardNumber(cardNumber);
        handler.setDescription(description);
        TransactionHandler.addTransactionCard(handler);
    }
}
