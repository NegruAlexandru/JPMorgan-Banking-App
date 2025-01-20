package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.app.userFacilities.Card;
import org.poo.utils.Operation;

public class DeleteCard extends Operation {
    public DeleteCard(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Deletes a card from the account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }

        User user1 = DB.findUserByEmail(handler.getEmail());
        Account userAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (userAccount == null) {
            return;
        }

        boolean found = false;
        for (Account acc : user1.getAccounts()) {
            if (acc.getIban().equals(userAccount.getIban())) {
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.getEmployees().contains(user1)
                && !businessAccount.getManagers().contains(user1)
                && !businessAccount.getOwner().equals(user1)) {
                return;
            }
        } else {
            if (!account.getEmail().equals(handler.getEmail())) {
                return;
            }
        }

        if (account.getBalance() > 0) {
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (businessAccount.getEmployees().contains(DB.findUserByEmail(handler.getEmail()))) {
                Card card = DB.getCardByCardNumber(handler.getCardNumber());

                if (card == null)
                    return;

                if (card.getType().equals("classic") && account.getBalance() > 0) {
                    return;
                }

                if (card.getEmailOfCreator().equals(handler.getEmail())) {
                    addTransaction("The card has been destroyed", account);
                    account.deleteCard(handler.getCardNumber());
//                    System.out.println("The card has been destroyed " + handler.getCardNumber());
//                    System.out.println("timestamp: " + handler.getTimestamp());
                }
            }
        } else {
            Card card = DB.getCardByCardNumber(handler.getCardNumber());

            if (card == null)
                return;

            if (card.getType().equals("classic") && account.getBalance() > 0) {
                return;
            }

            addTransaction("The card has been destroyed", account);
            account.deleteCard(handler.getCardNumber());
//            System.out.println("The card has been destroyed " + handler.getCardNumber());
//            System.out.println("timestamp: " + handler.getTimestamp());
        }
    }

    public void addTransaction(final String description, final Account account) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionCard(handler);
    }
}
