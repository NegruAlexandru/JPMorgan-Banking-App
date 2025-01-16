package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.input.User;
import org.poo.app.user_facilities.BusinessAccount;

public abstract class TransactionHandler {
    /**
     * Add a transaction with a description and a timestamp
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionDescriptionTimestamp(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for sending money
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionSendMoney(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction senderTransaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .receiverIBAN(commandHandler.getReceiver())
                .senderIBAN(commandHandler.getAccount())
                .transferType("sent")
                .amount(commandHandler.getAmount() + " "
                        + DB.findAccountByIBAN(commandHandler.getAccount()).getCurrency())
                .build();
        senderTransaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for receiving money
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionReceiveMoney(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction receiverTransaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .receiverIBAN(commandHandler.getReceiver())
                .senderIBAN(commandHandler.getAccount())
                .transferType("received")
                .amount(commandHandler.getAmount() + " "
                        + DB.findAccountByIBAN(commandHandler.getReceiver()).getCurrency())
                .build();
        receiverTransaction.addTransaction(DB.findAccountByIBAN(commandHandler.getReceiver()));
    }

    /**
     * Add a transaction for modifying a card
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionCard(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .card(commandHandler.getCardNumber())
                .cardHolder(commandHandler.getEmail())
                .account(commandHandler.getAccount())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for paying online
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionPayOnline(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .commerciant(commandHandler.getCommerciant())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for splitting a payment
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionSplitPaymentEqual(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .currency(commandHandler.getCurrency())
                .involvedAccounts(commandHandler.getAccounts())
                .splitPaymentType(commandHandler.getSplitPaymentType())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addTransactionSplitPaymentCustom(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountForUsers(commandHandler.getAmountForUsers())
                .currency(commandHandler.getCurrency())
                .involvedAccounts(commandHandler.getAccounts())
                .splitPaymentType(commandHandler.getSplitPaymentType())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for an error in splitting a payment
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionErrorSplitPaymentEqual(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .currency(commandHandler.getCurrency())
                .involvedAccounts(commandHandler.getAccounts())
                .errorMessage(commandHandler.getErrorMessage())
                .splitPaymentType(commandHandler.getSplitPaymentType())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addTransactionErrorSplitPaymentCustom(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountForUsers(commandHandler.getAmountForUsers())
                .currency(commandHandler.getCurrency())
                .involvedAccounts(commandHandler.getAccounts())
                .errorMessage(commandHandler.getErrorMessage())
                .splitPaymentType(commandHandler.getSplitPaymentType())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for withdrawing money
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionWithdrawMoney(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .classicAccountIBAN(commandHandler.getClassicAccountIBAN())
                .savingsAccountIBAN(commandHandler.getSavingsAccountIBAN())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addBusinessPayOnlineTransaction(final CommandHandler commandHandler, final User user) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .businessAssociatedUser(user)
                .amountDouble(commandHandler.getAmount())
                .commerciant(commandHandler.getCommerciant())
                .build();
        transaction.addBusinessTransaction((BusinessAccount) DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addUpgradePlanTransactionToDB(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .newPlanType(commandHandler.getNewPlanType())
                .accountIBAN(commandHandler.getAccount())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));

        System.out.println(DB.findAccountByIBAN(commandHandler.getAccount()).getTransactions().getLast());
    }

    public static void cashWithdrawalTransactionToDB(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addInterestTransactionToDB(final CommandHandler commandHandler) {
//        commandHandler.setAmount(Math.round(commandHandler.getAmount() * 100.0) / 100.0);

        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription(), commandHandler.getCommand())
                .amountDouble(commandHandler.getAmount())
                .currency(commandHandler.getCurrency())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }
}
