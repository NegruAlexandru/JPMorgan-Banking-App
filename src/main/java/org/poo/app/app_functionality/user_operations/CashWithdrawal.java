package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.*;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.utils.Operation;

public class CashWithdrawal extends Operation {
    public CashWithdrawal(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Withdraw money from an account
     */
    @Override
    public void execute() {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());

        if (card == null) {
            //Card not found
            addTransactionToOutput("description", "Card not found");
            return;
        }


        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            //Account not found

            return;
        }

        handler.setAccount(account.getIban());

        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            //User not found
//            addTransactionToDB("User not found");
            addTransactionToOutput("description", "User not found");
            return;
        }

        double amount = DB.convert(handler.getAmount(), "RON", account.getCurrency());
        amount = PaymentHandler.getAmountAfterFees(user, account, amount);

        if (account.getBalance() < amount) {
            //Insufficient funds
            super.addTransactionToDB("Insufficient funds");
            return;
        }

        addTransactionToDB("Cash withdrawal of " + handler.getAmount());

        AccountHandler.removeFunds(account, amount);
    }

    public void addTransactionToDB(final String description) {
        handler.setDescription(description);
        TransactionHandler.cashWithdrawalTransactionToDB(handler);
    }
}
