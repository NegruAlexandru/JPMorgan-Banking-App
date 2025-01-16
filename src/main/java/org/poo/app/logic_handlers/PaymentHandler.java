package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.user_operations.CreateOneTimeCard;
import org.poo.app.app_functionality.user_operations.DeleteCard;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.Discount;

public abstract class PaymentHandler {
    /**
     * Process a payment using the account and card
     *
     * @param ownerAccount   the account that owns the card
     * @param card           the card used for the transaction
     * @param commandHandler the current CommandHandler object
     */
    public static boolean pay(final Account ownerAccount,
                           final Card card, final CommandHandler commandHandler) {
        User user = DB.findUserByEmail(commandHandler.getEmail());

        double amount = commandHandler.getAmount();
        double amountAfterFees = PaymentHandler.getAmountAfterFees(user, ownerAccount, amount);

        double balance = ownerAccount.getBalance();
        commandHandler.setAccount(ownerAccount.getIban());

        if (balance - amountAfterFees < 0) {
            commandHandler.setDescription("Insufficient funds");
            System.out.println("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);

            return false;
        } else if (balance - ownerAccount.getMinBalance() - amountAfterFees <= 0) {
            commandHandler.setDescription("The card is frozen");
            System.out.println("The card is frozen");
            card.setCardStatus("frozen");
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);

            return false;
        } else {
            AccountHandler.removeFunds(ownerAccount, amountAfterFees);
//            System.out.println("cashback");
            double cashback = PaymentHandler.getCashbackAmount(user, ownerAccount, amount, DB.getCommerciantByName(commandHandler.getCommerciant()));
//            System.out.println("cashback " + cashback);
            AccountHandler.addFunds(ownerAccount, cashback);
            System.out.println("Payment successful");

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

            return true;
        }
    }

    public static double getAmountAfterFees(final User user, final Account account, final double amount) {
        if (user.getPlan().equals("standard")) {
            return amount * 1.002;
        } else if (user.getPlan().equals("silver")) {
            double amountInRON = DB.convert(amount, account.getCurrency(), "RON");
            if (amountInRON >= 500) {
                return amount * 1.001;
            }
        }
        return amount;
    }

    public static double getCashbackAmount(final User user, final Account account , final double amount, final Commerciant commerciant) {
        double cashback = 0;

        String commerciantType = commerciant.getType();
        for (Discount discount : account.getCashbacks()) {
            if (discount.getCategory().equals(commerciantType)) {
                cashback = discount.getValue();
//                System.out.println("discount " + discount.getValue() + " " + discount.getCategory());
                account.getCashbacks().remove(discount);
                break;
            }
        }

        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            account.getTotalSpentToCommerciant().putIfAbsent(commerciant, 0.0);
            double totalSpent = account.getTotalSpentToCommerciant().get(commerciant);
            if (100 <= totalSpent && totalSpent < 300) {
                cashback += switch (user.getPlan()) {
                    case "standard", "student" -> 0.001;
                    case "silver" -> 0.003;
                    case "gold" -> 0.005;
                    default -> 0;
                };
            } else if (300 <= totalSpent && totalSpent < 500) {
                cashback += switch (user.getPlan()) {
                    case "standard", "student" -> 0.002;
                    case "silver" -> 0.004;
                    case "gold" -> 0.0055;
                    default -> 0;
                };
            } else if (totalSpent >= 500) {
                cashback += switch (user.getPlan()) {
                    case "standard", "student" -> 0.0025;
                    case "silver" -> 0.005;
                    case "gold" -> 0.007;
                    default -> 0;
                };
            }
        }

//        System.out.println("Cashback: " + cashback);
//        System.out.println("Fees: " + fees);
//        System.out.println("Amount: " + amount);
//        System.out.println("Amount after fees and cashback: " + amount * (1 - cashback + fees));

//        System.out.println("cashback per cent " + cashback);
        return amount * cashback;
    }
}