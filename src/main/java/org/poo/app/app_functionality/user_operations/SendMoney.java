package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.*;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;
import static org.poo.app.logic_handlers.PaymentHandler.giveDiscountIfEligible;

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

        if (handler.getAmount() <= 0) {
            return;
        }

        User user1 = DB.findUserByEmail(handler.getEmail());

        if (senderAccount.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) senderAccount;
            if (!businessAccount.getEmployees().contains(user1)
                && !businessAccount.getManagers().contains(user1)
                && !businessAccount.getOwner().equals(user1)) {
                return;
            }
        } else {
            if (!senderAccount.getEmail().equals(handler.getEmail())) {
                return;
            }
        }

        Account receiverAccount = DB.findAccountByIBAN(handler.getReceiver());

        if (receiverAccount == null) {
            receiverAccount = DB.findAccountByIBAN(DB.findUserByEmail(senderAccount.getEmail()).getAliases().get(handler.getAccount()));
        }

        double amountSent = handler.getAmount();
        double amountSentAfterFees = PaymentHandler.getAmountAfterFees(DB.findUserByEmail(senderAccount.getEmail()), senderAccount, amountSent);

        if (senderAccount.getBalance() - amountSentAfterFees < 0) {
            handler.setDescription("Insufficient funds");
            handler.setAccount(senderAccount.getIban());
            TransactionHandler.addTransactionDescriptionTimestamp(handler);
            return;
        }

        if (receiverAccount == null) {
            Commerciant commerciant = DB.getCommerciantByIBAN(handler.getReceiver());

            if (commerciant == null) {
                return;
            }

            if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                double amount = DB.convert(amountSent, senderAccount.getCurrency(), "RON");

                double totalSpent = senderAccount.getTotalSpent();
                totalSpent += amount;
                if (handler.getAccount().equals(ibannenorocit)) {
                    System.out.println("total spent: " + totalSpent);
                }
                senderAccount.setTotalSpent(totalSpent);
            }

            double cashback = PaymentHandler.getCashbackAmount(DB.findUserByEmail(senderAccount.getEmail()), senderAccount, amountSent, commerciant);

            if (commerciant.getCashbackStrategy().equals("nrOfTransactions")) {
                if (senderAccount.getNrOfTransactionsToCommerciant().containsKey(commerciant)) {
                    int nrOfTransactions = senderAccount.getNrOfTransactionsToCommerciant().get(commerciant);
                    nrOfTransactions++;
                    senderAccount.getNrOfTransactionsToCommerciant().put(commerciant, nrOfTransactions);
                    if (handler.getAccount().equals(ibannenorocit)) {
                        System.out.println("Nr of transactions: " + nrOfTransactions);
                    }
                    giveDiscountIfEligible(senderAccount, nrOfTransactions);
                } else {
                    senderAccount.getNrOfTransactionsToCommerciant().put(commerciant, 1);
                    if (handler.getAccount().equals(ibannenorocit)) {
                        System.out.println("Nr of transactions: " + 1);
                    }
                }
            }

            AccountHandler.removeFunds(senderAccount, amountSentAfterFees);
            AccountHandler.addFunds(senderAccount, cashback);

            TransactionHandler.addTransactionSendMoney(handler);

            if (senderAccount.getType().equals("business"))
                TransactionHandler.addBusinessPayOnlineTransaction(handler,
                        DB.findUserByEmail(handler.getEmail()));

            if (user1.getPlan().equals("silver")) {
                double amount = DB.convert(amountSent, senderAccount.getCurrency(), "RON");
                if (amount >= 300) {

                    user1.setNrOfTransactionsOver300RON(user1.getNrOfTransactionsOver300RON() + 1);
                    if (senderAccount.getIban().equals(ibannenorocit)) {
                        System.out.println("Transaction over 300 RON");
                        System.out.println(user1.getNrOfTransactionsOver300RON());
                    }

                    if (user1.getNrOfTransactionsOver300RON() == 5) {
                        user1.setPlan("gold");
                        handler.setNewPlanType(user1.getPlan());
                        handler.setAccount(senderAccount.getIban());
                        handler.setDescription("Upgrade plan");
                        TransactionHandler.addUpgradePlanTransactionToDB(handler);
                    }
                }
            }
        } else {
            double amountReceived = AccountHandler.transferFunds(senderAccount,
                    receiverAccount, amountSent);

            handler.setAmount(amountSent);
            TransactionHandler.addTransactionSendMoney(handler);
            handler.setAmount(amountReceived);
            TransactionHandler.addTransactionReceiveMoney(handler);

            if (senderAccount.getType().equals("business"))
                TransactionHandler.addBusinessPayOnlineTransaction(handler,
                        DB.findUserByEmail(handler.getEmail()));

            if (user1.getPlan().equals("silver")) {
                double amount = DB.convert(amountSent, senderAccount.getCurrency(), "RON");
                if (amount >= 300) {

                    user1.setNrOfTransactionsOver300RON(user1.getNrOfTransactionsOver300RON() + 1);
                    if (senderAccount.getIban().equals(ibannenorocit)) {
                        System.out.println("Transaction over 300 RON");
                        System.out.println(user1.getNrOfTransactionsOver300RON());
                    }

                    if (user1.getNrOfTransactionsOver300RON() == 5) {
                        user1.setPlan("gold");
                        handler.setNewPlanType(user1.getPlan());
                        handler.setAccount(senderAccount.getIban());
                        handler.setDescription("Upgrade plan");
                        TransactionHandler.addUpgradePlanTransactionToDB(handler);
                    }
                }
            }
        }
    }
}
