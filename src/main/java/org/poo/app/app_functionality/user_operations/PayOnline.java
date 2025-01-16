package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.PaymentHandler;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.Discount;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

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
            System.out.println("Card not found");
            return;
        }

        if (handler.getAmount() <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        Account ownerAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (ownerAccount == null) {
            System.out.println("Account not found");
            return;
        }

        handler.setAmount(DB.convert(handler.getAmount(), handler.getCurrency(),
                ownerAccount.getCurrency()));

        if (card.getCardStatus().equals("frozen")) {
            handler.setDescription("The card is frozen");
            System.out.println("The card is frozen");
            handler.setAccount(ownerAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        User user = DB.findUserByEmail(ownerAccount.getEmail());

        if (ownerAccount.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) ownerAccount;
            if (!businessAccount.getEmployees().contains(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                addTransactionToOutput("description", "Card not found");
                System.out.println("Card not found1");
                return;
            }

            double amount = handler.getAmount();
//            amount = PaymentHandler.getAmountAfterFees(user, ownerAccount, amount);

            if (!businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                if (amount > businessAccount.getSpendingLimit()) {
//                addTransactionToOutput("description", "Spending limit exceeded");
                    System.out.println("Spending limit exceeded");
                    return;
                }
            }

            user = DB.findUserByEmail(businessAccount.getOwner().getEmail());
        }

        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());
        if (commerciant == null) {
            System.out.println("Commerciant not found");
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

        if (PaymentHandler.pay(ownerAccount, card, handler)) {
            if (user.getPlan().equals("silver")) {
                if (amount >= 300) {

                    user.setNrOfTransactionsOver300RON(user.getNrOfTransactionsOver300RON() + 1);
                    if (ownerAccount.getIban().equals(ibannenorocit)) {
                        System.out.println("Transaction over 300 RON");
                        System.out.println(user.getNrOfTransactionsOver300RON());
                    }

                    if (user.getNrOfTransactionsOver300RON() == 5) {
//                    System.out.println("Upgrade plan to gold");
//                    System.out.println(handler);

                        user.setPlan("gold");
                        handler.setNewPlanType(user.getPlan());
                        handler.setAccount(ownerAccount.getIban());
                        handler.setDescription("Upgrade plan");
                        TransactionHandler.addUpgradePlanTransactionToDB(handler);
                    }
                }
            }
        } else {
            if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {

                // Update the total amount spent by the account, change it back
                if (ownerAccount.getTotalSpentToCommerciant().containsKey(commerciant)) {
                    double totalSpent = ownerAccount.getTotalSpentToCommerciant().get(commerciant);
                    totalSpent -= amount;
                    ownerAccount.getTotalSpentToCommerciant().put(commerciant, totalSpent);
                }
            }

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
    }
}
