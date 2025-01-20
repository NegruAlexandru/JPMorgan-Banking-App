package org.poo.app.payment;

import org.poo.app.baseClasses.Commerciant;
import org.poo.app.userFacilities.Account;
import org.poo.utils.CashbackStrategy;

import static org.poo.app.payment.PaymentHandler.giveDiscountIfEligible;

public class CashbackNrOfTransactions implements CashbackStrategy {
    private final double amount;
    private final Account account;
    private final Commerciant commerciant;

    public CashbackNrOfTransactions(final double amount,
                                    final Account account,
                                    final Commerciant commerciant) {
        this.amount = amount;
        this.account = account;
        this.commerciant = commerciant;
    }

     /**
     * Collects the cashback details for the account based on the current company's cashback policy
     */
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

        return cashback * amount;
    }
}
