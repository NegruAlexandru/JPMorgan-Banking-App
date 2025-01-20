package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.utils.Operation;

public class AddFunds extends Operation {
    public AddFunds(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Add funds to the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getEmployees().contains(DB.findUserByEmail(handler.getEmail()))) {
                return;
            }
            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                    && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                if (handler.getAmount() > businessAccount.getDepositLimit()) {
                    return;
                }
            }
        }

        AccountHandler.addFunds(account, handler.getAmount());

        if (account.getType().equals("business")) {
            handler.setAccount(account.getIban());
            TransactionHandler.addBusinessPayOnlineTransaction(handler,
                    DB.findUserByEmail(handler.getEmail()));
        }
    }
}
