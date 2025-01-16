package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.logic_handlers.*;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.app.user_facilities.Discount;
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

        if (senderAccount == null) {
            return;
        }

        if (handler.getReceiver().isEmpty()) {
            addTransactionToOutput("description", "User not found");
        }

        Account receiverAccount;
        if (handler.getAlias() != null) {
            receiverAccount = DB.findAccountByIBAN(DB.findUserByEmail(senderAccount.getEmail()).getAliases().get(handler.getAlias()));
        } else {
            receiverAccount = DB.findAccountByIBAN(handler.getReceiver());

            if (receiverAccount == null) {
                receiverAccount = DB.findAccountByIBAN(DB.findUserByEmail(senderAccount.getEmail()).getAliases().get(handler.getAccount()));
            }
//            if (receiverAccount == null && senderAccount.getType().equals("business")) {
//                // receiver account maybe is a commerciant
//
//                if (DB.getCommerciantByIBAN(handler.getReceiver()) != null) {
//                    // TODO: implement commerciant payment
//
//                    // use / add discount
//                }
//                return;
//
        }

        if (receiverAccount == null) {
            Commerciant commerciant = DB.getCommerciantByIBAN(handler.getReceiver());
            if (commerciant != null) {
                double amountSent = handler.getAmount();
                double amountSentAfterFees = PaymentHandler.getAmountAfterFees(DB.findUserByEmail(senderAccount.getEmail()), senderAccount, amountSent);

                if (senderAccount.getBalance() - amountSentAfterFees < 0) {
                    handler.setDescription("Insufficient funds");
                    handler.setAccount(senderAccount.getIban());
                    TransactionHandler.addTransactionDescriptionTimestamp(handler);
                    return;
                }

                if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                    // Update the total amount spent by the account
                    if (senderAccount.getTotalSpentToCommerciant().containsKey(commerciant)) {
                        double totalSpent = senderAccount.getTotalSpentToCommerciant().get(commerciant);
                        totalSpent += amountSent;
                        senderAccount.getTotalSpentToCommerciant().put(commerciant, totalSpent);
                    } else
                        senderAccount.getTotalSpentToCommerciant().put(commerciant, amountSent);
                } else if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {

                    // Update the number of transactions for the account
                    if (senderAccount.getNrOfTransactionsToCommerciant().containsKey(commerciant)) {
                        int nrOfTransactions = senderAccount.getNrOfTransactionsToCommerciant().get(commerciant);
                        nrOfTransactions++;
                        senderAccount.getNrOfTransactionsToCommerciant().put(commerciant, nrOfTransactions);
                    } else {
                        senderAccount.getNrOfTransactionsToCommerciant().put(commerciant, 1);
                    }

                    int nrOfTransactions = senderAccount.getNrOfTransactionsToCommerciant().get(commerciant);

                    // Add discount if the account has reached a certain number of transactions
                    Discount discount = null;
                    if (nrOfTransactions == 2)
                        discount = new Discount("Food", 0.02);
                    else if (nrOfTransactions == 5)
                        discount = new Discount("Clothes", 0.05);
                    else if (nrOfTransactions == 10)
                        discount = new Discount("Tech", 0.1);

                    if (discount != null)
                        senderAccount.getCashbacks().add(discount);
                }

                if (senderAccount.getType().equals("business")) {
                    BusinessAccount senderBusinessAccount = (BusinessAccount) senderAccount;
                    if (!senderBusinessAccount.getOwner().equals(DB.findUserByEmail(handler.getEmail()))) {
                        if (handler.getAmount() < senderBusinessAccount.getSpendingLimit()) {
                            System.out.println("Amount is less than the spending limit");
                            return;
                        }
                    }
                }

                double cashback = PaymentHandler.getCashbackAmount(DB.findUserByEmail(senderAccount.getEmail()), senderAccount, amountSent, commerciant);

                AccountHandler.removeFunds(senderAccount, amountSentAfterFees);
                AccountHandler.addFunds(senderAccount, cashback);

                handler.setAmount(amountSent);
                TransactionHandler.addTransactionSendMoney(handler);

                if (senderAccount.getType().equals("business")) {
                    TransactionHandler.addBusinessPayOnlineTransaction(handler,
                            DB.findUserByEmail(handler.getEmail()));
                }
            }
            return;
        }


        double amountSent = handler.getAmount();
        double amountSentAfterFees = PaymentHandler.getAmountAfterFees(DB.findUserByEmail(senderAccount.getEmail()), senderAccount, amountSent);

        if (senderAccount.getBalance() - amountSentAfterFees < 0) {
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

        if (senderAccount.getType().equals("business")) {
            TransactionHandler.addBusinessPayOnlineTransaction(handler,
                    DB.findUserByEmail(handler.getEmail()));
        }
    }
}
