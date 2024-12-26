package org.poo.app.logic_handlers;

import org.poo.app.app_functionality.debug.Transaction;

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
        transaction.addTransaction(DB.findAccountByCardNumber(commandHandler.getCardNumber()));
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
                .build();
        transaction.addTransaction(DB.findAccountByIBAN(commandHandler.getAccount()));
    }
}
