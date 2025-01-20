package org.poo.app.input;

import org.poo.utils.CashbackStrategy;

public class Context {
    public void processCashbackDetails(CashbackStrategy strategy) {
        strategy.collectCashbackDetails();
    }
}
