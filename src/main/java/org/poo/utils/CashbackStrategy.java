package org.poo.utils;

public interface CashbackStrategy {
    /**
     * Collects the cashback details for the account based on the current company's cashback policy
     */
    void collectCashbackDetails();
    /**
     * Computes and returns the available cashback for the account
     * @return the cashback
     */
    double getAvailableCashback();
}
