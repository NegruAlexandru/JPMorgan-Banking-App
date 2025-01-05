package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.PaymentHandler;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.Discount;
import org.poo.utils.Operation;

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
            addTransactionToOutput("description", "Card not found");
            return;
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

        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());
        if (commerciant == null) {
            return;
        }

        double amount = DB.convert(handler.getAmount(), ownerAccount.getCurrency(), "RON");

        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {

            // Update the total amount spent by the account
            if (ownerAccount.getTotalSpentToCommerciant().containsKey(commerciant)) {
                double totalSpent = ownerAccount.getTotalSpentToCommerciant().get(commerciant);
                totalSpent += amount;
                ownerAccount.getTotalSpentToCommerciant().put(commerciant, totalSpent);
            } else
                ownerAccount.getTotalSpentToCommerciant().put(commerciant, amount);
        }

        double amountBefore = handler.getAmount();

        PaymentHandler.pay(ownerAccount, card, handler);

        // Treat better the case when the payment is not successful
        if (handler.getAmount() == amountBefore) {
            return;
        }

        if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {

            // Update the number of transactions for the account
            if (ownerAccount.getNrOfTransactionsToCommerciant().containsKey(commerciant)) {
                int nrOfTransactions = ownerAccount.getNrOfTransactionsToCommerciant().get(commerciant);
                nrOfTransactions++;
                ownerAccount.getNrOfTransactionsToCommerciant().put(commerciant, nrOfTransactions);
            } else {
                ownerAccount.getNrOfTransactionsToCommerciant().put(commerciant, 1);
            }

            int nrOfTransactions = ownerAccount.getNrOfTransactionsToCommerciant().get(commerciant);

            // Add discount if the account has reached a certain number of transactions
            Discount discount = null;
            if (nrOfTransactions == 2)
                discount = new Discount("Food", 0.02);
            else if (nrOfTransactions == 5)
                discount = new Discount("Clothes", 0.05);
            else if (nrOfTransactions == 10)
                discount = new Discount("Tech", 0.1);

            if (discount != null)
                ownerAccount.getCashbacks().add(discount);
        }

        if (amount >= 300) {
            User user = DB.findUserByEmail(ownerAccount.getEmail());
            user.setNrOfTransactionsOver300RON(user.getNrOfTransactionsOver300RON() + 1);

            if (user.getNrOfTransactionsOver300RON() == 5) {
                user.setPlan("gold");
            }
        }
    }
}
