package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.payment.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.baseClasses.User;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

import static org.poo.app.logicHandlers.TransactionHandler.addUpgradePlanTransactionToDB;

public class UpgradePlan extends Operation {
    private static final int SILVER_TO_GOLD = 250;
    private static final int STANDARD_TO_SILVER = 100;
    private static final int STANDARD_TO_GOLD = 350;

    public UpgradePlan(final CommandHandler handler,
                       final ArrayNode output) {
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
                sumToPay = STANDARD_TO_SILVER;
            } else if (handler.getNewPlanType().equals("gold")) {
                sumToPay = STANDARD_TO_GOLD;
            }
        } else if (user.getPlan().equals("silver")) {
            if (handler.getNewPlanType().equals("gold")) {
                sumToPay = SILVER_TO_GOLD;
            }
        }
        return sumToPay;
    }
}
