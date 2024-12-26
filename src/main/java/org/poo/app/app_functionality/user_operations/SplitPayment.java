package org.poo.app.app_functionality.user_operations;

import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;

public abstract class SplitPayment {
    /**
     * Search for account without funds for split payment
     * @param handler current CommandHandler object
     * @return last account without funds or null if all accounts have funds
     */
    private static Account searchForAccountWithoutFundsForSplit(final CommandHandler handler) {
        Account accountWithoutFunds = null;
        for (String account : handler.getAccounts()) {
            Account accountForSplit = DB.findAccountByIBAN(account);
            double amountToPay = handler.getAmount() / handler.getAccounts().size();

            if (accountForSplit == null) {
                return accountWithoutFunds;
            }

            ExchangeRate exchangeRate = DB.getExchangeRate(handler.getCurrency(),
                    accountForSplit.getCurrency());
            amountToPay = amountToPay * exchangeRate.getRate();

            if (accountForSplit.getBalance() - amountToPay < 0) {
                accountWithoutFunds = accountForSplit;
            }
        }
        return accountWithoutFunds;
    }

    /**
     * Create transaction if split payment is invalid
     * @param handler current CommandHandler object
     * @return true if split payment is invalid
     */
    private static boolean createTransactionIfSplitInvalid(final CommandHandler handler) {
        Account accountWithoutFunds = searchForAccountWithoutFundsForSplit(handler);
        if (accountWithoutFunds != null) {
            double amountToPay = handler.getAmount() / handler.getAccounts().size();
            handler.setDescription("Split payment of "
                    + String.format("%.2f", handler.getAmount()) + " " + handler.getCurrency());
            handler.setAmount(amountToPay);
            handler.setErrorMessage("Account "
                    + accountWithoutFunds.getIban()
                    + " has insufficient funds for a split payment.");

            for (String acc : handler.getAccounts()) {
                Account accForSplit = DB.findAccountByIBAN(acc);
                handler.setAccount(accForSplit.getIban());
                handler.setEmail(accForSplit.getEmail());
                TransactionHandler.addTransactionErrorSplitPayment(handler);
            }

            return true;
        }

        return false;
    }

    /**
     * Executes the split payment
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        if (createTransactionIfSplitInvalid(handler)) {
            return;
        }
        double initialAmount = handler.getAmount();

        for (String account : handler.getAccounts()) {
            Account accountForSplit = DB.findAccountByIBAN(account);
            double amountToPay = initialAmount / handler.getAccounts().size();
            handler.setAmount(amountToPay);

            amountToPay = DB.convert(amountToPay,
                    handler.getCurrency(), accountForSplit.getCurrency());

            AccountHandler.removeFunds(accountForSplit, amountToPay);
            handler.setEmail(accountForSplit.getEmail());
            handler.setDescription("Split payment of "
                    + String.format("%.2f", initialAmount) + " " + handler.getCurrency());
            handler.setAccount(accountForSplit.getIban());
            TransactionHandler.addTransactionSplitPayment(handler);
        }
    }
}
