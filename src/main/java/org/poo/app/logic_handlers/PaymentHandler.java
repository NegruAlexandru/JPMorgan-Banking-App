package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.user_operations.CreateOneTimeCard;
import org.poo.app.app_functionality.user_operations.DeleteCard;
import org.poo.app.input.Commerciant;
import org.poo.app.input.ExchangeRate;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;

import java.util.HashMap;

public abstract class PaymentHandler {
    /**
     * Process a payment using the account and card
     * @param ownerAccount the account that owns the card
     * @param card the card used for the transaction
     * @param commandHandler the current CommandHandler object
     */
    public static void pay(final Account ownerAccount,
                           final Card card, final CommandHandler commandHandler) {
        User user = DB.findUserByEmail(commandHandler.getEmail());
        Commerciant commerciant = DB.getCommerciantByName(commandHandler.getCommerciant());
        double amount = getAmountAfterFees(user, commandHandler.getAmount());

        double balance = ownerAccount.getBalance();
        commandHandler.setAccount(ownerAccount.getIban());

        if (balance - amount < 0) {
            commandHandler.setDescription("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);
        } else if (balance - ownerAccount.getMinBalance() - amount <= 0) {
            commandHandler.setDescription("The card is frozen");
            card.setCardStatus("frozen");
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);
        } else {
            ownerAccount.setBalance(balance - amount);
            commandHandler.setDescription("Card payment");
            TransactionHandler.addTransactionPayOnline(commandHandler);
            if (card.getType().equals("one-time")) {

                new DeleteCard(commandHandler, null).execute();
                new CreateOneTimeCard(commandHandler, null).execute();
            }

            if (ownerAccount.getType().equals("business")) {
                TransactionHandler.addBusinessPayOnlineTransaction(commandHandler,
                        DB.findUserByEmail(commandHandler.getEmail()));
            }
        }
    }

    public static double getAmountAfterFees(final User user, final double amount) {
        if (user.getPlan().equals("standard")) {
            return amount * 1.002;
        } else if (user.getPlan().equals("silver")) {
            if (amount >= 500) {
                return amount * 1.001;
            }
        }
        return amount;
    }

    public static double getAmountAfterFeesAndCashback(final User user,
                                                       final Commerciant commerciant,
                                                       final double amount) {

        HashMap<Double, HashMap<String, Double>> cashbackMap = new HashMap<>();
        cashbackMap.put(100.0, new HashMap<>());
        cashbackMap.get(100.0).put("standard", 0.1);
        cashbackMap.get(100.0).put("student", 0.1);
        cashbackMap.get(100.0).put("silver", 0.3);
        cashbackMap.get(100.0).put("gold", 0.5);

        cashbackMap.put(300.0, new HashMap<>());
        cashbackMap.get(300.0).put("standard", 0.2);
        cashbackMap.get(300.0).put("student", 0.2);
        cashbackMap.get(300.0).put("silver", 0.4);
        cashbackMap.get(300.0).put("gold", 0.55);

        cashbackMap.put(500.0, new HashMap<>());
        cashbackMap.get(500.0).put("standard", 0.25);
        cashbackMap.get(500.0).put("student", 0.25);
        cashbackMap.get(500.0).put("silver", 0.5);
        cashbackMap.get(500.0).put("gold", 0.7);


        double cashback = 0;
        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            if (amount >= 500) {
                cashback = cashbackMap.get(500.0).get(user.getPlan());
            } else if (amount >= 300) {
                cashback = cashbackMap.get(300.0).get(user.getPlan());
            } else if (amount >= 100) {
                cashback = cashbackMap.get(100.0).get(user.getPlan());
            }
        }


        if (user.getPlan().equals("standard")) {
            return amount * 1.002;
        } else if (user.getPlan().equals("silver")) {
            if (amount >= 500) {
                return amount * 1.001;
            }
        }
        return amount;
    }
}
