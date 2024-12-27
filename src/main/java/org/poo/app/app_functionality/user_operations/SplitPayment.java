package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

public class SplitPayment extends Operation {
    public SplitPayment(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Executes the split payment
     */
    @Override
    public void execute() {
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
            addTransaction("Split payment of "
                    + String.format("%.2f", initialAmount) + " " + handler.getCurrency(),
                    accountForSplit);
        }
    }

    public void addTransaction(final String description, final Account account) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionSplitPayment(handler);
    }

    /**
     * Search for account without funds for split payment
     * @param handler current CommandHandler object
     * @return last account without funds or null if all accounts have funds
     */
    private Account searchForAccountWithoutFundsForSplit(final CommandHandler handler) {
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
    private boolean createTransactionIfSplitInvalid(final CommandHandler handler) {
        Account accountWithoutFunds = searchForAccountWithoutFundsForSplit(handler);
        if (accountWithoutFunds != null) {
            double amountToPay = handler.getAmount() / handler.getAccounts().size();

            for (String acc : handler.getAccounts()) {
                Account accForSplit = DB.findAccountByIBAN(acc);
                addTransaction("Split payment of "
                        + String.format("%.2f", handler.getAmount()) + " " + handler.getCurrency(),
                        "Account " + accountWithoutFunds.getIban()
                                + " has insufficient funds for a split payment.",
                        accForSplit, amountToPay);
            }

            return true;
        }

        return false;
    }

    public void addTransaction(final String description, final String errorMessage,
                               final Account account, final double amount) {
        handler.setDescription(description);
        handler.setEmail(account.getEmail());
        handler.setAccount(account.getIban());
        handler.setAmount(amount);
        handler.setErrorMessage(errorMessage);
        TransactionHandler.addTransactionSplitPayment(handler);
    }
}
