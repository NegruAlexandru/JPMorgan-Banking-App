package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
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
            System.out.println("Account not found addfunds");
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                System.out.println("You are not allowed to deposit funds into this account");
                return;
            }
            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                    && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                if (handler.getAmount() < businessAccount.getDepositLimit()) {
                    System.out.println("Amount is less than the spending limit");
                    return;
                }
            }
        }

        AccountHandler.addFunds(account, handler.getAmount());
        System.out.println("Funds added successfully");

        if (account.getType().equals("business")) {
            handler.setAccount(account.getIban());
            TransactionHandler.addBusinessPayOnlineTransaction(handler,
                    DB.findUserByEmail(handler.getEmail()));
        }
    }
}
