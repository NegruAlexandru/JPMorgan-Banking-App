package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;

public class SetMinimumBalance extends Operation {
    public SetMinimumBalance(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Set the minimum balance for the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        account.setMinBalance(handler.getAmount());
    }
}
