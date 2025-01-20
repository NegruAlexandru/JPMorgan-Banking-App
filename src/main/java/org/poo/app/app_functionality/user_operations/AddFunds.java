package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

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
            if (account.getIban().equals(ibannenorocit)) {
                System.out.println("Account not found addfunds");
            }
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))
                && !businessAccount.getEmployees().contains(DB.findUserByEmail(handler.getEmail()))
                ) {
                if (account.getIban().equals(ibannenorocit)) {
                    System.out.println("You are not allowed to deposit funds into this account");
                }
                return;
            }
            if (!businessAccount.getManagers().contains(DB.findUserByEmail(handler.getEmail()))
                    && !businessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                if (account.getIban().equals(ibannenorocit)) {
                    System.out.println("amount" + handler.getAmount() + " " + businessAccount.getDepositLimit());
                }
                if (handler.getAmount() > businessAccount.getDepositLimit()) {
                    if (account.getIban().equals(ibannenorocit)) {
                        System.out.println("Amount is less than the deposit limit");
                    }
                    return;
                }
            }
        }

        AccountHandler.addFunds(account, handler.getAmount());
        if (account.getIban().equals(ibannenorocit)) {
            System.out.println("Funds added successfully");
        }

//        if (account.getType().equals("business")) {
//            BusinessAccount businessAccount = (BusinessAccount) account;
//            User user = DB.findUserByEmail(account.getEmail());
//            User user1 = DB.findUserByEmail(handler.getEmail());
//            if (businessAccount.getIban().equals("RO32POOB0130532963818359")
//                    || businessAccount.getIban().equals("RO98POOB8412955460158769")) {
//                System.out.println("command: " + handler.getCommand());
//                System.out.println("account: " + businessAccount.getIban());
//                System.out.println("user: " + businessAccount.getEmail());
//                System.out.println("amount to deposit :" + handler.getAmount());
//                System.out.println("deposit limit " + businessAccount.getDepositLimit());
//                System.out.println("balance: " + businessAccount.getBalance());
//                System.out.println("user plan: " + user.getPlan());
//                if (businessAccount.getManagers().contains(user1)) {
//                    System.out.println("manager");
//                } else if (businessAccount.getOwner().equals(user1)) {
//                    System.out.println("owner");
//                } else if (businessAccount.getEmployees().contains(user1)) {
//                    System.out.println("employee");
//                }
//                System.out.println("timestamp: " + handler.getTimestamp() + "\n");
//            }
//        }

        if (account.getType().equals("business")) {
            handler.setAccount(account.getIban());
            TransactionHandler.addBusinessPayOnlineTransaction(handler,
                    DB.findUserByEmail(handler.getEmail()));
        }
    }
}
