package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.app.user_facilities.SavingsAccount;
import org.poo.utils.Operation;

public class AddNewBusinessAssociate extends Operation {
    public AddNewBusinessAssociate(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }
    /**
     * Adds a new business associate to the business account
     *
     */
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());
        // account - care de business
        // email - of associate

        if (account == null) {
            return;
        }

        if (!account.getType().equals("business")) {
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;

        User user = DB.findUserByEmail(handler.getEmail());

        if (handler.getRole().equals("employee")) {
            new AccountHandler().addNewEmployee(businessAccount, user);
        } else if (handler.getRole().equals("manager")) {
            new AccountHandler().addNewManager(businessAccount, user);
        }
    }
}
