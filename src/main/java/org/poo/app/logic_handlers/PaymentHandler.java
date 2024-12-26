package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.user_operations.CreateOneTimeCard;
import org.poo.app.app_functionality.user_operations.DeleteCard;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;

public abstract class PaymentHandler {
    /**
     * Process a payment using the account and card
     * @param ownerAccount the account that owns the card
     * @param card the card used for the transaction
     * @param commandHandler the current CommandHandler object
     */
    public static void pay(final Account ownerAccount,
                           final Card card, final CommandHandler commandHandler) {
        double amount = commandHandler.getAmount();
        double balance = ownerAccount.getBalance();

        if (balance - amount < 0) {
            commandHandler.setDescription("Insufficient funds");
            commandHandler.setAccount(ownerAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);
        } else if (balance - ownerAccount.getMinBalance() - amount <= 0) {
            commandHandler.setDescription("The card is frozen");
            card.setCardStatus("frozen");
            commandHandler.setAccount(ownerAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);
        } else {
            ownerAccount.setBalance(balance - amount);
            commandHandler.setDescription("Card payment");
            TransactionHandler.addTransactionPayOnline(commandHandler);
            if (card.getType().equals("one-time")) {
                commandHandler.setAccount(ownerAccount.getIban());
                DeleteCard.execute(commandHandler);
                CreateOneTimeCard.execute(commandHandler);
            }

        }
    }
}
