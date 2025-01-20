package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.input.ExchangeRate;
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
import static org.poo.app.logic_handlers.PaymentHandler.giveDiscountIfEligible;

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

        if (handler.getAmount() <= 0) {
            return;
        }

        Account ownerAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (ownerAccount == null) {
            return;
        }

        User user1 = DB.findUserByEmail(handler.getEmail());

        if (ownerAccount.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) ownerAccount;
            if (!businessAccount.getEmployees().contains(user1)
                && !businessAccount.getManagers().contains(user1)
                && !businessAccount.getOwner().equals(user1)) {
                addTransactionToOutput("description", "Card not found");
                return;
            }

            if (!businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                if (handler.getAmount() > businessAccount.getSpendingLimit()) {
                    if (ownerAccount.getIban().equals(ibannenorocit)) {
                        System.out.println("Spending limit exceeded");
                    }
                    return;
                }
            }
        } else {
            if (!ownerAccount.getEmail().equals(handler.getEmail())) {
                addTransactionToOutput("description", "Card not found");
                return;
            }
        }

        handler.setAmount(DB.convert(handler.getAmount(), handler.getCurrency(),
                ownerAccount.getCurrency()));

        User user = DB.findUserByEmail(ownerAccount.getEmail());

        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());
        if (commerciant == null) {
            if (ownerAccount.getIban().equals(ibannenorocit)) {
                System.out.println("Commerciant not found");
            }
            return;
        }

        double amount = DB.convert(handler.getAmount(), ownerAccount.getCurrency(), "RON");

        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            double totalSpent = ownerAccount.getTotalSpent();
            totalSpent += amount;
            if (ownerAccount.getIban().equals(ibannenorocit)) {
                System.out.println("Total spent: " + totalSpent);
            }
            ownerAccount.setTotalSpent(totalSpent);
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
                double totalSpent = ownerAccount.getTotalSpent();
                totalSpent -= amount;
                ownerAccount.setTotalSpent(totalSpent);
            }

            return;
        }

        if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {

            // Update the number of transactions for the account
            if (ownerAccount.getNrOfTransactionsToCommerciant().containsKey(commerciant)) {
                int nrOfTransactions = ownerAccount.getNrOfTransactionsToCommerciant().get(commerciant);
                nrOfTransactions++;
                ownerAccount.getNrOfTransactionsToCommerciant().put(commerciant, nrOfTransactions);
                if (ownerAccount.getIban().equals(ibannenorocit)) {
                    System.out.println("Nr of transactions: " + nrOfTransactions);
                }

                giveDiscountIfEligible(ownerAccount, nrOfTransactions);
            } else {
                ownerAccount.getNrOfTransactionsToCommerciant().put(commerciant, 1);
            }
        }
    }
}
