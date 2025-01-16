package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.*;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;
import java.time.Year;

public class WithdrawSavings extends Operation {
    public WithdrawSavings(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Withdraw money from a savings account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            //Account not found
            return;
        }

        handler.setAccount(account.getIban());

        if (!account.getType().equals("savings")) {
            //Account is not of type savings.
            addTransactionToDB("Account is not of type savings");
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
            addTransactionToDB("You don't have the minimum age required.");
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
            addTransactionToDB("You do not have a classic account.");
            return;
        }

        double amount = DB.convert(handler.getAmount(), handler.getCurrency(), account.getCurrency());
//        amount = PaymentHandler.getAmountAfterFees(user, account, amount);

        if (account.getBalance() < amount) {
            //Insufficient funds
            addTransactionToDB("Insufficient funds");
            return;
        }

//        AccountHandler.transferFunds(account, account1, handler.getAmount());
        AccountHandler.addFunds(account1, handler.getAmount());
        AccountHandler.removeFunds(account, amount);
        addTransaction("Savings withdrawal", account, account1);
    }

    public void addTransaction(final String description, final Account account,
                               final Account account1) {
        handler.setDescription(description);
        handler.setSavingsAccountIBAN(account.getIban());
        handler.setClassicAccountIBAN(account1.getIban());

        handler.setAccount(account1.getIban());
        TransactionHandler.addTransactionWithdrawMoney(handler);

        ExchangeRate exchangeRate = DB.getExchangeRate(account.getCurrency(), handler.getCurrency());
        handler.setAmount(handler.getAmount() * exchangeRate.getRate());
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionWithdrawMoney(handler);
    }
}