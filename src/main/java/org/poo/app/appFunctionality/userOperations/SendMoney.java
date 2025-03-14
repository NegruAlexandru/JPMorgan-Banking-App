package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.baseClasses.User;
import org.poo.app.baseClasses.Commerciant;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.payment.PaymentHandler;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.payment.AccountHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.utils.Operation;

public class SendMoney extends Operation {
    public SendMoney(final CommandHandler handler,
                     final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Send money from one account to another
     */
    @Override
    public void execute() {
        Account senderAccount = DB.findAccountByIBAN(handler.getAccount());

        if (senderAccount == null) {
            return;
        }

        if (handler.getReceiver().isEmpty()) {
            addTransactionToOutput("description", "User not found");
        }

        if (handler.getAmount() <= 0) {
            return;
        }

        User user = DB.findUserByEmail(handler.getEmail());

        if (senderAccount.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) senderAccount;
            if (!businessAccount.getEmployees().contains(user)
                && !businessAccount.getManagers().contains(user)
                && !businessAccount.getOwner().equals(user)) {
                return;
            }
        } else {
            if (!senderAccount.getEmail().equals(handler.getEmail())) {
                return;
            }
        }

        double amountSent = handler.getAmount();
        double amountSentAfterFees = PaymentHandler.getAmountAfterFees(
                DB.findUserByEmail(senderAccount.getEmail()),
                senderAccount,
                amountSent);

        if (senderAccount.getBalance() - amountSentAfterFees < 0) {
            handler.setDescription("Insufficient funds");
            handler.setAccount(senderAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        Account receiverAccount = DB.findAccountByIBAN(handler.getReceiver());

        if (receiverAccount == null) {
            receiverAccount = DB.findAccountByIBAN(
                    DB.findUserByEmail(senderAccount.getEmail())
                            .getAliases().get(handler.getAccount()));
        }

        if (receiverAccount == null) {
            Commerciant commerciant = DB.getCommerciantByIBAN(handler.getReceiver());

            if (commerciant == null) {
                return;
            }

            double cashback = PaymentHandler.getCashback(
                    amountSent,
                    senderAccount,
                    commerciant,
                    user);

            AccountHandler.removeFunds(
                    senderAccount,
                    amountSentAfterFees);
            AccountHandler.addFunds(
                    senderAccount,
                    cashback);

            TransactionHandler.addTransactionSendMoney(handler);

        } else {
            double amountReceived = AccountHandler.transferFunds(
                    senderAccount,
                    receiverAccount,
                    amountSent);

            handler.setAmount(amountSent);
            TransactionHandler.addTransactionSendMoney(handler);
            handler.setAmount(amountReceived);
            TransactionHandler.addTransactionReceiveMoney(handler);

        }

        if (senderAccount.getType().equals("business")) {
            handler.setAccount(senderAccount.getIban());
            TransactionHandler.addBusinessPayOnlineTransaction(
                    handler,
                    DB.findUserByEmail(handler.getEmail()));
        }
        AccountHandler.incrementAndCheckIfPlanUpgradeIsAvailable(
                user,
                senderAccount,
                amountSent,
                handler);
    }
}
