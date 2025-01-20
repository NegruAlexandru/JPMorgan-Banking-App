package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.payment.ExchangeRate;
import org.poo.app.baseClasses.User;
import org.poo.app.payment.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;
import java.time.Year;

public class WithdrawSavings extends Operation {
    private static final int MINIMUM_AGE = 21;

    public WithdrawSavings(final CommandHandler handler,
                           final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Withdraw money from a savings account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        handler.setAccount(account.getIban());

        if (!account.getType().equals("savings")) {
            addTransactionToDB("Account is not of type savings");
            return;
        }

        User user = DB.findUserByEmail(account.getEmail());

        if (user == null) {
            return;
        }

        int year = Integer.parseInt(user.getBirthDate().split("-")[0]);
        int currentYear = Year.now().getValue();

        if (currentYear - year < MINIMUM_AGE) {
            addTransactionToDB("You don't have the minimum age required.");
            return;
        }

        boolean hasClassic = false;
        Account account1 = null;
        for (Account acc : user.getAccounts()) {
            if (acc.getType().equals("classic")
                && acc.getCurrency().equals(account.getCurrency())) {
                hasClassic = true;
                account1 = acc;
                break;
            }
        }

        if (!hasClassic) {
            addTransactionToDB("You do not have a classic account.");
            return;
        }

        double amount = DB.convert(
                handler.getAmount(),
                handler.getCurrency(),
                account.getCurrency());

        if (account.getBalance() < amount) {
            addTransactionToDB("Insufficient funds");
            return;
        }

        AccountHandler.addFunds(account1, handler.getAmount());
        AccountHandler.removeFunds(account, amount);
        addTransaction(
                "Savings withdrawal",
                account,
                account1);
    }

    /**
     * Adds a transaction to the database
     * @param description the description of the transaction
     */
    public void addTransaction(final String description,
                               final Account account,
                               final Account account1) {
        handler.setDescription(description);
        handler.setSavingsAccountIBAN(account.getIban());
        handler.setClassicAccountIBAN(account1.getIban());

        handler.setAccount(account1.getIban());
        TransactionHandler.addTransactionWithdrawMoney(handler);

        ExchangeRate exchangeRate = DB.getExchangeRate(
                account.getCurrency(),
                handler.getCurrency());
        handler.setAmount(handler.getAmount() * exchangeRate.getRate());
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionWithdrawMoney(handler);
    }
}
