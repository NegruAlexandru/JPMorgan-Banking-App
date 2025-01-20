package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.input.User;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

public class AddAccount extends Operation {
    public AddAccount(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Adds a new account to the user
     */
    @Override
    public void execute() {
        User user = DB.findUserByEmail(handler.getEmail());

        Account newAccount;
        switch (handler.getAccountType()) {
            case "savings" -> newAccount = user.addSavingsAccount(handler.getCurrency(), handler.getInterestRate());
            case "classic" -> newAccount = user.addAccount(handler.getCurrency());
            case "business" -> newAccount = user.addBusinessAccount(handler.getCurrency());
            default -> {
                return;
            }
        }

        addTransaction("New account created", newAccount);
    }

    public void addTransaction(final String description, final Account account) {
        handler.setAccount(account.getIban());
        handler.setDescription(description);
        TransactionHandler.addTransactionDescriptionTimestamp(handler);
    }
}
