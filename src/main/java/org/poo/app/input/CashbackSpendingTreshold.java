package org.poo.app.input;

import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Discount;
import org.poo.utils.CashbackStrategy;

public class CashbackSpendingTreshold implements CashbackStrategy {
    private final double amount;
    private final Account account;
    private final Commerciant commerciant;
    private final User user;

    public CashbackSpendingTreshold(double amount, Account account, Commerciant commerciant, User user) {
        this.amount = amount;
        this.account = account;
        this.commerciant = commerciant;
        this.user = user;
    }

    @Override
    public void collectCashbackDetails() {
        double amountInRon = DB.convert(amount, account.getCurrency(), "RON");

        double totalSpent = account.getTotalSpent();
        totalSpent += amountInRon;
        account.setTotalSpent(totalSpent);
    }

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

        return cashback * amount;
    }
}