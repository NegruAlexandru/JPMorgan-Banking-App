package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.input.Commerciant;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.PaymentHandler;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.Discount;
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

        PaymentHandler.pay(ownerAccount, card, handler);

        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());
        if (commerciant == null) {
            return;
        }

        double amount = DB.convert(handler.getAmount(), ownerAccount.getCurrency(), "RON");

        if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
            if (commerciant.getNrOfTransactionsOfUsers().containsKey(ownerAccount)) {
                int nrOfTransactions = commerciant.getNrOfTransactionsOfUsers().get(ownerAccount);
                nrOfTransactions++;
                commerciant.getNrOfTransactionsOfUsers().put(ownerAccount, nrOfTransactions);
            } else {
                commerciant.getNrOfTransactionsOfUsers().put(ownerAccount, 1);
            }

            int nrOfTransactions = commerciant.getNrOfTransactionsOfUsers().get(ownerAccount);

            Discount discount = null;
            if (nrOfTransactions == 2)
                discount = new Discount("Food", 0.02);
            else if (nrOfTransactions == 5)
                discount = new Discount("Clothes", 0.05);
            else if (nrOfTransactions == 10)
                discount = new Discount("Tech", 0.1);

            if (discount != null)
                ownerAccount.getDiscounts().add(discount);

        } else if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            if (commerciant.getTotalSpentByUsers().containsKey(ownerAccount)) {
                double totalSpent = commerciant.getTotalSpentByUsers().get(ownerAccount);
                totalSpent += amount;
                commerciant.getTotalSpentByUsers().put(ownerAccount, totalSpent);
            } else
                commerciant.getTotalSpentByUsers().put(ownerAccount, amount);
        }
    }
}
