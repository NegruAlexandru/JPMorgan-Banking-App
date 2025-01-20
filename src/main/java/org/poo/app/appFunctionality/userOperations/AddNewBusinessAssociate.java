package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.baseClasses.User;
import org.poo.app.payment.AccountHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.utils.Operation;

public class AddNewBusinessAssociate extends Operation {
    public AddNewBusinessAssociate(final CommandHandler handler,
                                   final ArrayNode output) {
        super(handler, output);
    }
    /**
     * Adds a new business associate to the business account
     *
     */
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        if (!account.getType().equals("business")) {
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;

        User user = DB.findUserByEmail(handler.getEmail());

        if (businessAccount.getEmployees().contains(user)
            || businessAccount.getManagers().contains(user)
            || businessAccount.getOwner().equals(user)) {
            return;
        }

        if (handler.getRole().equals("employee")) {
            new AccountHandler().addNewEmployee(businessAccount, user);
        } else if (handler.getRole().equals("manager")) {
            new AccountHandler().addNewManager(businessAccount, user);
        }
    }
}
