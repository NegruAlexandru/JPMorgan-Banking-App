package org.poo.app.payment;

import org.poo.app.baseClasses.Commerciant;
import org.poo.app.baseClasses.User;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.utils.CashbackStrategy;

public class CashbackSpendingTreshold implements CashbackStrategy {
    private final double amount;
    private final Account account;
    private final Commerciant commerciant;
    private final User user;
    private static final int THRESHOLD_1 = 100;
    private static final int THRESHOLD_2 = 300;
    private static final int THRESHOLD_3 = 500;
    private static final double CASHBACK_1_STANDARD = 0.001;
    private static final double CASHBACK_1_SILVER = 0.003;
    private static final double CASHBACK_1_GOLD = 0.005;
    private static final double CASHBACK_2_STANDARD = 0.002;
    private static final double CASHBACK_2_SILVER = 0.004;
    private static final double CASHBACK_2_GOLD = 0.0055;
    private static final double CASHBACK_3_STANDARD = 0.0025;
    private static final double CASHBACK_3_SILVER = 0.005;
    private static final double CASHBACK_3_GOLD = 0.007;

    public CashbackSpendingTreshold(final double amount,
                                    final Account account,
                                    final Commerciant commerciant,
                                    final User user) {
        this.amount = amount;
        this.account = account;
        this.commerciant = commerciant;
        this.user = user;
    }

    /**
     * Collects the cashback details for the account based on the current company's cashback policy
     */
    @Override
    public void collectCashbackDetails() {
        double amountInRon = DB.convert(amount, account.getCurrency(), "RON");

        double totalSpent = account.getTotalSpent();
        totalSpent += amountInRon;
        account.setTotalSpent(totalSpent);
    }

    /**
     * Computes and returns the available cashback for the account
     * @return the cashback
     */
    @Override
    public double getAvailableCashback() {
        double cashback = 0;

        String commerciantType = commerciant.getType();
        for (Discount discount : account.getCashbacks()) {
            if (discount.getCategory().equals(commerciantType)) {
                if (!discount.isUsed()) {
                    cashback = discount.getValue();

                    discount.setUsed(true);
                    break;
                }
            }
        }

        double totalSpent = account.getTotalSpent();
        if (THRESHOLD_1 <= totalSpent && totalSpent < THRESHOLD_2) {
            cashback += switch (user.getPlan()) {
                case "standard", "student" -> CASHBACK_1_STANDARD;
                case "silver" -> CASHBACK_1_SILVER;
                case "gold" -> CASHBACK_1_GOLD;
                default -> 0;
            };
        } else if (THRESHOLD_2 <= totalSpent && totalSpent < THRESHOLD_3) {
            cashback += switch (user.getPlan()) {
                case "standard", "student" -> CASHBACK_2_STANDARD;
                case "silver" -> CASHBACK_2_SILVER;
                case "gold" -> CASHBACK_2_GOLD;
                default -> 0;
            };
        } else if (totalSpent >= THRESHOLD_3) {
            cashback += switch (user.getPlan()) {
                case "standard", "student" -> CASHBACK_3_STANDARD;
                case "silver" -> CASHBACK_3_SILVER;
                case "gold" -> CASHBACK_3_GOLD;
                default -> 0;
            };
        }

        return cashback * amount;
    }
}
