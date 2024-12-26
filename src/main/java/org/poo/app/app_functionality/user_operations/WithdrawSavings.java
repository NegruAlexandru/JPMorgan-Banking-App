package org.poo.app.app_functionality.user_operations;

import org.poo.app.input.ExchangeRate;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.*;
import org.poo.app.user_facilities.Account;
import java.time.Year;

public abstract class WithdrawSavings {
    /**
     * Withdraw money from a savings account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            //Account not found
            return;
        }

        handler.setAccount(account.getIban());

        if (!account.getType().equals("savings")) {
            //Account is not of type savings.
            handler.setDescription("Account is not of type savings");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        User user = DB.findUserByEmail(account.getEmail());
        if (user == null) {
            return;
        }

        int year = Integer.parseInt(user.getBirthDate().split("-")[0]);
        int currentYear = Year.now().getValue();

        if (currentYear - year < 21) {
            //You don't have the minimum age required.
            handler.setDescription("You don't have the minimum age required.");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        boolean hasClassic = false;
        Account account1 = null;
        for (Account acc : user.getAccounts()) {
            if (acc.getType().equals("classic") && acc.getCurrency().equals(account.getCurrency())) {
                hasClassic = true;
                account1 = acc;
                break;
            }
        }

        if (!hasClassic) {
            //You do not have a classic account.
            handler.setDescription("You do not have a classic account.");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        ExchangeRate exchangeRate = DB.getExchangeRate(account.getCurrency(), handler.getCurrency());

        if (account.getBalance() < handler.getAmount() * exchangeRate.getRate()) {
            //Insufficient funds
            handler.setDescription("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        AccountHandler.transferFunds(account, account1, handler.getAmount());
        handler.setDescription("Savings withdrawal");
        handler.setAmount(handler.getAmount() * exchangeRate.getRate());
        handler.setSavingsAccountIBAN(account.getIban());
        handler.setClassicAccountIBAN(account1.getIban());
        TransactionHandler.addTransactionWithdrawMoney(handler);
    }
}