package org.poo.app.app_functionality.user_operations;

import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

public abstract class CashWithdrawal {
    /**
     * Withdraw money from an account
     * @param handler current CommandHandler object
     */
    public static void execute(final CommandHandler handler) {
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        // ???
        if (account == null) {
            //Account not found
            handler.setDescription("Account not found");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        handler.setAccount(account.getIban());

        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            //User not found
            handler.setDescription("User not found");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        if (account.getBalance() < handler.getAmount()) {
            //Insufficient funds
            handler.setDescription("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        account.setBalance(account.getBalance() - handler.getAmount());
        handler.setDescription("Cash withdrawal of " + handler.getAmount());
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }
}
