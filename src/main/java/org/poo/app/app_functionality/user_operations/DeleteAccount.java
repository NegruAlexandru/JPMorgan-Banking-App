package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public abstract class DeleteAccount {
    /**
     * Deletes an account
     * @param handler current CommandHandler object
     */
    public static ObjectNode execute(final CommandHandler handler) {
        User user = DB.findUserByEmail(handler.getEmail());
        Account account = DB.findAccountByIBAN(handler.getAccount());

        ObjectNode output = OBJECT_MAPPER.createObjectNode();

        if (account.getBalance() == 0) {
            user.deleteAccount(account);
            output.put("success", "Account deleted");
            output.put("timestamp", handler.getTimestamp());
        } else {
            output.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
            output.put("timestamp", handler.getTimestamp());
            handler.setDescription("Account couldn't be deleted - there are funds remaining");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
        }

        return output;
    }
}
