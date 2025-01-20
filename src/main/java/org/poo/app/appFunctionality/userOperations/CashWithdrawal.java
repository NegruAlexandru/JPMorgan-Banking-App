package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.payment.PaymentHandler;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.payment.AccountHandler;
import org.poo.app.baseClasses.User;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.utils.Operation;

public class CashWithdrawal extends Operation {
    public CashWithdrawal(final CommandHandler handler,
                          final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Withdraw money from an account
     */
    @Override
    public void execute() {
        if (DB.getCardByCardNumber(handler.getCardNumber()) == null) {
            addTransactionToOutput("description", "Card not found");
            return;
        }

        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            return;
        }

        User user = DB.findUserByEmail(handler.getEmail());

        if (user == null) {
            addTransactionToOutput("description", "User not found");
            return;
        }

        if (!account.getEmail().equals(handler.getEmail())) {
            addTransactionToOutput("description", "Card not found");
            return;
        }

        double amount = DB.convert(handler.getAmount(), "RON", account.getCurrency());
        double amountAfterFees = PaymentHandler.getAmountAfterFees(user, account, amount);

        if (account.getBalance() < amountAfterFees) {
            handler.setAccount(account.getIban());
            super.addTransactionToDB("Insufficient funds");
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.getManagers().contains(user)
                && !businessAccount.getOwner().equals(user)
                && businessAccount.getEmployees().contains(user)) {
                if (amountAfterFees < businessAccount.getSpendingLimit()) {
                    return;
                }
            }
        }

        addTransactionToDB("Cash withdrawal of " + handler.getAmount(), account.getIban());

        AccountHandler.removeFunds(account, amountAfterFees);
    }

    /**
     * Add a transaction to the database
     * @param description the description of the transaction
     * @param iban the iban of the account
     */
    private void addTransactionToDB(final String description,
                                    final String iban) {
        handler.setDescription(description);
        handler.setAccount(iban);
        TransactionHandler.cashWithdrawalTransactionToDB(handler);
    }
}
