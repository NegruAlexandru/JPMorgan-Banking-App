package org.poo.app.payment;

import org.poo.utils.CashbackStrategy;

public class Context {
    /**
     * Process cashback details
     * @param strategy Cashback strategy
     */
    public void processCashbackDetails(final CashbackStrategy strategy) {
        strategy.collectCashbackDetails();
    }
}
