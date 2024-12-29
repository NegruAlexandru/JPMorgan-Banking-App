package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
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
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        // ???
//        if (account == null) {
//            //Account not found
//            addTransaction("Account not found");
//            return;
//        }

        if (account == null) {
            //Account not found

            return;
        }

        handler.setAccount(account.getIban());

        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            //User not found
            addTransactionToDB("User not found");
            return;
        }

        if (account.getBalance() < handler.getAmount()) {
            //Insufficient funds
            addTransactionToDB("Insufficient funds");
            return;
        }

        AccountHandler.removeFunds(account, handler.getAmount());
        addTransactionToDB("Cash withdrawal of " + handler.getAmount());
    }
}
