package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.input.User;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

public class DeleteAccount extends Operation {
    public DeleteAccount(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Deletes an account
     */
    @Override
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        if (account.getBalance() == 0) {
            user.deleteAccount(account);
            addTransactionToOutput("success", "Account deleted");
        } else {
            addTransactionToDB("Account couldn't be deleted - there are funds remaining");
            addTransactionToOutput("error", "Account couldn't be deleted - see org.poo.transactions for details");
        }
    }
}
