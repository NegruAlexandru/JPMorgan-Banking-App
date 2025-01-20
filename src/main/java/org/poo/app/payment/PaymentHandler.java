package org.poo.app.payment;

import org.poo.app.appFunctionality.userOperations.CreateOneTimeCard;
import org.poo.app.baseClasses.User;
import org.poo.app.baseClasses.Commerciant;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Card;
import org.poo.utils.CashbackStrategy;

public class PaymentHandler {
    private final Account account;
    private final CommandHandler handler;
    private static final String CATEGORY_1 = "Food";
    private static final String CATEGORY_2 = "Clothes";
    private static final String CATEGORY_3 = "Tech";
    private static final double VALUE_1 = 0.02;
    private static final double VALUE_2 = 0.05;
    private static final double VALUE_3 = 0.1;
    private static final int NR_OF_TRANSACTIONS_1 = 2;
    private static final int NR_OF_TRANSACTIONS_2 = 5;
    private static final int NR_OF_TRANSACTIONS_3 = 10;
    private static final String CASHBACK_STRATEGY_1 = "spendingThreshold";
    private static final String CASHBACK_STRATEGY_2 = "nrOfTransactions";
    private static final String PLAN_STANDARD = "standard";
    private static final String PLAN_SILVER = "silver";
    private static final double COMMISSION_STANDARD = 0.002;
    private static final double COMMISSION_SILVER = 0.001;
    private static final int COMMISSION_SILVER_THRESHOLD = 500;
    private static final String THRESHOLD_CURRENCY = "RON";

    public PaymentHandler(final Account account,
                          final CommandHandler handler) {
        this.account = account;
        this.handler = handler;
    }

    /**
     * Process a payment using the account and card
     */
    public void pay() {
        User user = DB.findUserByEmail(account.getEmail());

        Card card = DB.getCardByCardNumber(handler.getCardNumber());

        double amount = handler.getAmount();
        double amountAfterFees = PaymentHandler.getAmountAfterFees(
                user,
                account,
                amount);
        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());

        double cashback = PaymentHandler.getCashback(
                amount,
                account,
                commerciant,
                user);

        AccountHandler.removeFunds(account, amountAfterFees);
        AccountHandler.addFunds(account, cashback);

        handler.setDescription("Card payment");
        TransactionHandler.addTransactionPayOnline(handler);

        double amountInRon = DB.convert(
                handler.getAmount(),
                account.getCurrency(),
                "RON");
        AccountHandler.incrementAndCheckIfPlanUpgradeIsAvailable(
                user,
                account,
                amountInRon,
                handler);

        if (account.getType().equals("business")) {
            TransactionHandler.addBusinessPayOnlineTransaction(
                    handler,
                    DB.findUserByEmail(handler.getEmail()));
        }

        if (card.getType().equals("one-time")) {
            account.deleteCard(handler.getCardNumber());
            addTransaction();
            new CreateOneTimeCard(handler, null).execute();
        }
    }

    /**
     * Adds a transaction to the database
     */
    private void addTransaction() {
        handler.setEmail(account.getEmail());
        handler.setDescription("The card has been destroyed");
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(handler);
    }

    /**
     * Returns the amount to pay after fees (commission) is applied
     * @param user the user
     * @param account the account
     * @param amount the amount before fees
     * @return the amount after fees
     */
    public static double getAmountAfterFees(final User user,
                                            final Account account,
                                            final double amount) {
        if (user.getPlan().equals(PLAN_STANDARD)) {
            return amount + COMMISSION_STANDARD * amount;
        } else if (user.getPlan().equals(PLAN_SILVER)) {
            double amountInCurrencyNeeded = DB.convert(
                    amount,
                    account.getCurrency(),
                    THRESHOLD_CURRENCY);
            if (amountInCurrencyNeeded >= COMMISSION_SILVER_THRESHOLD) {
                return amount + COMMISSION_SILVER * amount;
            }
        }
        return amount;
    }

    /**
     * Gives a discount to the account if the user is eligible
     * @param account the account
     * @param nrOfTransactions the number of transactions
     */
    public static void giveDiscountIfEligible(final Account account,
                                              final int nrOfTransactions) {
        switch (nrOfTransactions) {
            case NR_OF_TRANSACTIONS_1 -> makeCheckAndGiveDiscount(
                    account,
                    CATEGORY_1,
                    VALUE_1);
            case NR_OF_TRANSACTIONS_2 -> makeCheckAndGiveDiscount(
                    account,
                    CATEGORY_2,
                    VALUE_2);
            case NR_OF_TRANSACTIONS_3 -> makeCheckAndGiveDiscount(
                    account,
                    CATEGORY_3,
                    VALUE_3);
            default -> {
            }
        }
    }

    /**
     * Makes a check and gives a discount to the account if the user is eligible
     * @param account the account
     * @param category the category
     * @param value the value
     */
    private static void makeCheckAndGiveDiscount(final Account account,
                                                 final String category,
                                                 final double value) {
        boolean found = false;
        for (Discount d : account.getCashbacks()) {
            if (d.getCategory().equals(category)) {
                found = true;
                break;
            }
        }

        if (!found) {
            account.getCashbacks().add(new Discount(category, value));
        }
    }

    /**
     * Returns the cashback for a payment
     * @param amount the amount
     * @param ownerAccount the owner account
     * @param commerciant the commerciant
     * @param user the user
     * @return the cashback
     */
    public static double getCashback(final double amount,
                                     final Account ownerAccount,
                                     final Commerciant commerciant,
                                     final User user) {
        double cashback = 0;
        if (commerciant.getCashbackStrategy().equals(CASHBACK_STRATEGY_1)) {
            CashbackStrategy cashbackStrategy = new CashbackSpendingTreshold(
                    amount,
                    ownerAccount,
                    commerciant,
                    user);

            new Context().processCashbackDetails(cashbackStrategy);

            cashback = cashbackStrategy.getAvailableCashback();
        } else if (commerciant.getCashbackStrategy().equals(CASHBACK_STRATEGY_2)) {
            CashbackStrategy cashbackStrategy = new CashbackNrOfTransactions(
                    amount,
                    ownerAccount,
                    commerciant);

            cashback = cashbackStrategy.getAvailableCashback();

            new Context().processCashbackDetails(cashbackStrategy);
        }

        return cashback;
    }
}
