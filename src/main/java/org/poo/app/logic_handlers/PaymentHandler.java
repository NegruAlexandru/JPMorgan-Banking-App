package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.user_operations.CreateOneTimeCard;
import org.poo.app.app_functionality.user_operations.DeleteCard;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.Discount;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

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
        User user = DB.findUserByEmail(ownerAccount.getEmail());

        double amount = commandHandler.getAmount();
        double amountAfterFees = PaymentHandler.getAmountAfterFees(user, ownerAccount, amount);

        double balance = ownerAccount.getBalance();
        commandHandler.setAccount(ownerAccount.getIban());

        if (balance - amountAfterFees < 0) {
            commandHandler.setDescription("Insufficient funds");
            if (ownerAccount.getIban().equals(ibannenorocit)) {
                System.out.println("Insufficient funds");
            }
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);

            return false;
        } else if (balance - ownerAccount.getMinBalance() - amountAfterFees <= 0) {
            commandHandler.setDescription("The card is frozen");
            if (ownerAccount.getIban().equals(ibannenorocit)) {
                System.out.println("The card is frozen");
            }
            card.setCardStatus("frozen");
            TransactionHandler.addTransactionDescriptionTimestamp(commandHandler);

            return false;
        } else {
            AccountHandler.removeFunds(ownerAccount, amountAfterFees);
            double cashback = PaymentHandler.getCashbackAmount(user, ownerAccount, amount, DB.getCommerciantByName(commandHandler.getCommerciant()));
            AccountHandler.addFunds(ownerAccount, cashback);
            if (ownerAccount.getIban().equals(ibannenorocit)) {
                System.out.println("Payment successful");
            }

            commandHandler.setDescription("Card payment");
            TransactionHandler.addTransactionPayOnline(commandHandler);

            if (ownerAccount.getType().equals("business")) {
                TransactionHandler.addBusinessPayOnlineTransaction(commandHandler,
                        DB.findUserByEmail(commandHandler.getEmail()));
            }

            if (card.getType().equals("one-time")) {
                ownerAccount.deleteCard(commandHandler.getCardNumber());
                addTransaction("The card has been destroyed", ownerAccount, commandHandler);
                new CreateOneTimeCard(commandHandler, null).execute();
            }

            return true;
        }
    }

    public static void addTransaction(final String description, final Account account, final CommandHandler commandHandler) {
        commandHandler.setEmail(account.getEmail());
        commandHandler.setDescription(description);
        commandHandler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(commandHandler);
    }

    public static double getAmountAfterFees(final User user, final Account account, final double amount) {
        if (user.getPlan().equals("standard")) {
            return amount * 1.002;
        } else if (user.getPlan().equals("silver")) {
            double amountInRON = DB.convert(amount, account.getCurrency(), "RON");
            System.out.println("amount in RON " + amountInRON);
            if (amountInRON >= 500) {
                return amount * 1.001;
            }
        }
        return amount;
    }

    public static double getCashbackAmount(final User user, final Account account, final double amount, final Commerciant commerciant) {
        double cashback = 0;

        String commerciantType = commerciant.getType();
        for (Discount discount : account.getCashbacks()) {
            if (discount.getCategory().equals(commerciantType)) {
                if (!discount.isUsed()) {
                    cashback = discount.getValue();
                    if (account.getIban().equals(ibannenorocit)) {
                        System.out.println("discount " + discount.getValue() + " " + discount.getCategory());
                    }

                    discount.setUsed(true);
                    break;
                }
            }
        }

        if (account.getIban().equals(ibannenorocit)) {
            System.out.println("commerciant type " + commerciantType + " commerciant cashback strategy " + commerciant.getCashbackStrategy());
            System.out.println("user plan " + user.getPlan());
        }

        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            double totalSpent = account.getTotalSpent();
            if (account.getIban().equals(ibannenorocit)) {
                System.out.println("total spent " + totalSpent);
            }
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

        if (account.getIban().equals(ibannenorocit)) {
            System.out.println("cashback " + cashback);
            System.out.println("cashback amount " + amount * cashback);
        }

        return amount * cashback;
    }

    public static void giveDiscountIfEligible(final Account account, final int nrOfTransactions) {
        switch (nrOfTransactions) {
            case 2 -> makeCheckAndGiveDiscount(account, "Food", 0.02);
            case 5 -> makeCheckAndGiveDiscount(account, "Clothes", 0.05);
            case 10 -> makeCheckAndGiveDiscount(account, "Tech", 0.1);
        }
    }

    private static void makeCheckAndGiveDiscount(final Account account, final String category, final double value) {
        boolean found = false;
        for (Discount d : account.getCashbacks()) {
            if (d.getCategory().equals(category)) {
                found = true;
                break;
            }
        }

        if (!found)
            account.getCashbacks().add(new Discount(category, value));
    }
}