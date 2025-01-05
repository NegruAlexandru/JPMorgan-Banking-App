package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;

public abstract class TransactionHandler {
    /**
     * Add a transaction with a description and a timestamp
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionDescriptionTimestamp(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for sending money
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionSendMoney(final CommandHandler commandHandler) {
        Transaction senderTransaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
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
        Transaction receiverTransaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
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
                commandHandler.getDescription())
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
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
                .commerciant(commandHandler.getCommerciant())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    /**
     * Add a transaction for splitting a payment
     * @param commandHandler current CommandHandler object
     */
    public static void addTransactionSplitPayment(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
                .currency(commandHandler.getCurrency())
                .involvedAccounts(commandHandler.getAccounts())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addTransactionSplitPaymentCustom(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
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
    public static void addTransactionErrorSplitPayment(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
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
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
                .classicAccountIBAN(commandHandler.getClassicAccountIBAN())
                .savingsAccountIBAN(commandHandler.getSavingsAccountIBAN())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addBusinessPayOnlineTransaction(final CommandHandler commandHandler, final User user) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .businessAssociatedUser(user)
                .amountDouble(commandHandler.getAmount())
                .commerciant(commandHandler.getCommerciant())
                .build();
        transaction.addTransaction(DB.findAccountByCardNumber(commandHandler.getCardNumber()));
    }

    public static void addUpgradePlanTransactionToDB(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .newPlanType(commandHandler.getNewPlanType())
                .accountIBAN(commandHandler.getAccount())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void cashWithdrawalTransactionToDB(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }

    public static void addInterestTransactionToDB(final CommandHandler commandHandler) {
        Transaction transaction = new Transaction.Builder(commandHandler.getTimestamp(),
                commandHandler.getDescription())
                .amountDouble(commandHandler.getAmount())
                .currency(commandHandler.getCurrency())
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }
}
