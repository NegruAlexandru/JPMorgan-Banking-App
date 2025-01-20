package org.poo.app.input;

import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Discount;
import org.poo.utils.CashbackStrategy;

import static org.poo.app.logicHandlers.PaymentHandler.giveDiscountIfEligible;

public class CashbackNrOfTransactions implements CashbackStrategy {
    private final double amount;
    private final Account account;
    private final Commerciant commerciant;

    public CashbackNrOfTransactions(double amount, Account account, Commerciant commerciant) {
        this.amount = amount;
        this.account = account;
        this.commerciant = commerciant;
    }

    @Override
    public void collectCashbackDetails() {
        if (account.getNrOfTransactionsToCommerciant().containsKey(commerciant)) {
            int nrOfTransactions = account.getNrOfTransactionsToCommerciant().get(commerciant);
            nrOfTransactions++;
            account.getNrOfTransactionsToCommerciant().put(commerciant, nrOfTransactions);
            giveDiscountIfEligible(account, nrOfTransactions);
        } else {
            account.getNrOfTransactionsToCommerciant().put(commerciant, 1);
        }
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

        return cashback * amount;
    }
}