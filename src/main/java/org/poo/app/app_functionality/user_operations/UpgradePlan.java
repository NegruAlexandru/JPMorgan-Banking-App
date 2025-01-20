package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.TransactionHandler.addUpgradePlanTransactionToDB;

public class UpgradePlan extends Operation {
    public UpgradePlan(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Upgrade the plan of a user
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            addTransactionToOutput("description", "Account not found");
            return;
        }

        User user = DB.findUserByEmail(account.getEmail());

        if (user == null) {
            //User not found
            return;
        }

        if (user.getPlan().equals(handler.getNewPlanType())) {
            //The user already has the ${newPlanType} plan.
            addTransactionToDB("The user already has the " + handler.getNewPlanType() + " plan.");
            return;
        }

        double sumToPay = calculateSumToPay(user, handler);
        sumToPay = DB.convert(sumToPay, "RON", account.getCurrency());

        if (sumToPay == 0) {
            //You cannot downgrade your plan.
            addTransactionToDB("You cannot downgrade your plan.");
            return;
        }

        if (account.getBalance() < sumToPay) {
            //Insufficient funds
            addTransactionToDB("Insufficient funds");
            return;
        }

        AccountHandler.removeFunds(account, sumToPay);
        user.setPlan(handler.getNewPlanType());

        handler.setDescription("Upgrade plan");
        addUpgradePlanTransactionToDB(handler);
    }

    private int calculateSumToPay(final User user, final CommandHandler handler) {
        int sumToPay = 0;
        if (user.getPlan().equals("student") || user.getPlan().equals("standard")) {
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
