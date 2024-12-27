package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.logic_handlers.AccountHandler;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.logic_handlers.TransactionHandler;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;

public class SendMoney extends Operation {
    public SendMoney(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Send money from one account to another
     */
    @Override
    public void execute() {
        Account senderAccount = DB.findAccountByIBAN(handler.getAccount());
        Account receiverAccount;
        if (handler.getAlias() != null) {
            receiverAccount = DB.findAccountByIBAN(DB.findUserByEmail(senderAccount.getEmail()).getAliases().get(handler.getAlias()));
        } else {
            receiverAccount = DB.findAccountByIBAN(handler.getReceiver());
        }

        if (senderAccount == null || receiverAccount == null) {
            return;
        }

        double amountSent = handler.getAmount();

        if (senderAccount.getBalance() - amountSent < 0) {
            handler.setDescription("Insufficient funds");
            handler.setAccount(senderAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        double amountReceived = AccountHandler.transferFunds(senderAccount,
                receiverAccount, amountSent);

        handler.setAmount(amountSent);
        TransactionHandler.addTransactionSendMoney(handler);
        handler.setAmount(amountReceived);
        TransactionHandler.addTransactionReceiveMoney(handler);
    }
}
