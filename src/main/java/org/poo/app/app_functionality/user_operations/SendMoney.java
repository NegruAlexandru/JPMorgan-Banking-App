package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.*;
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

            if (receiverAccount == null && senderAccount.getType().equals("business")) {
                // receiver account maybe is a commerciant

                if (DB.getCommerciantByIBAN(handler.getReceiver()) != null) {
                    // TODO: implement commerciant payment

                    // use / add discount
                }
                return;
            }
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
