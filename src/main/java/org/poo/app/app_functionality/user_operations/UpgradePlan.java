package org.poo.app.app_functionality.user_operations;

import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

public abstract class UpgradePlan {
    /**
     * Withdraw money from an account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        // ???
        if (account == null) {
            //Account not found
            handler.setDescription("Account not found");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        handler.setAccount(account.getIban());

        User user = DB.findUserByEmail(account.getEmail());

        if (user == null) {
            //User not found
            return;
        }

        int sumToPay = calculateSumToPay(user, handler);

        if (user.getPlan().equals(handler.getNewPlanType())) {
            //The user already has the ${newPlanType} plan.
            handler.setDescription("The user already has the " + handler.getNewPlanType() + " plan.");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        if (sumToPay == 0) {
            //You cannot downgrade your plan.
            handler.setDescription("You cannot downgrade your plan.");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        ExchangeRate exchangeRate = DB.getExchangeRate(account.getCurrency(), "RON");

        if (account.getBalance() < sumToPay * exchangeRate.getRate()) {
            //Insufficient funds
            handler.setDescription("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        account.setBalance(account.getBalance() - sumToPay * exchangeRate.getRate());
        user.setPlan(handler.getNewPlanType());
        handler.setDescription("Upgrade plan");
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }

    private static int calculateSumToPay(final User user, final CommandHandler handler) {
        int sumToPay = 0;
        if (user.getPlan().equals("student") || user.getPlan().equals("classic")) {
            if (handler.getNewPlanType().equals("silver")) {
                sumToPay = 100;
            } else if (handler.getNewPlanType().equals("gold")) {
                sumToPay = 350;
            }
        } else if (user.getPlan().equals("silver")) {
            if (handler.getNewPlanType().equals("gold")) {
                sumToPay = 250;
            }
        }
        return sumToPay;
    }
}
