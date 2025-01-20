package org.poo.app.logicHandlers;

import org.poo.app.appFunctionality.userOperations.CreateOneTimeCard;
import org.poo.app.input.*;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Card;
import org.poo.app.userFacilities.Discount;
import org.poo.utils.CashbackStrategy;

public abstract class PaymentHandler {
    /**
     * Process a payment using the account and card
     *
     * @param ownerAccount   the account that owns the card
     * @param card           the card used for the transaction
     * @param handler the current CommandHandler object
     */
    public static void pay(final Account ownerAccount,
                           final Card card, final CommandHandler handler) {
        User user = DB.findUserByEmail(ownerAccount.getEmail());

        double amount = handler.getAmount();
        double amountAfterFees = PaymentHandler.getAmountAfterFees(user, ownerAccount, amount);
        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());

        double cashback = PaymentHandler.getCashback(amount, ownerAccount, commerciant, user);

        AccountHandler.removeFunds(ownerAccount, amountAfterFees);
        AccountHandler.addFunds(ownerAccount, cashback);

        handler.setDescription("Card payment");
        TransactionHandler.addTransactionPayOnline(handler);

        double amountInRon = DB.convert(handler.getAmount(), ownerAccount.getCurrency(), "RON");
        AccountHandler.incrementAndCheckIfPlanUpgradeIsAvailable(user, ownerAccount, amountInRon, handler);

        if (ownerAccount.getType().equals("business")) {
            TransactionHandler.addBusinessPayOnlineTransaction(handler,
                    DB.findUserByEmail(handler.getEmail()));
        }

        if (card.getType().equals("one-time")) {
            ownerAccount.deleteCard(handler.getCardNumber());
            addTransaction("The card has been destroyed", ownerAccount, handler);
            new CreateOneTimeCard(handler, null).execute();
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
            if (amountInRON >= 500) {
                return amount * 1.001;
            }
        }
        return amount;
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

    public static double getCashback(final double amount, final Account ownerAccount, final Commerciant commerciant, final User user) {
        double cashback = 0;
        if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
            CashbackStrategy cashbackStrategy = new CashbackSpendingTreshold(amount, ownerAccount, commerciant, user);

            new Context().processCashbackDetails(cashbackStrategy);

            cashback = cashbackStrategy.getAvailableCashback();
        } else if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
            CashbackStrategy cashbackStrategy = new CashbackNrOfTransactions(amount, ownerAccount, commerciant);

            cashback = cashbackStrategy.getAvailableCashback();

            new Context().processCashbackDetails(cashbackStrategy);
        }

        return cashback;
    }
}