package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import org.poo.app.baseClasses.User;
import org.poo.app.payment.AccountHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.payment.PaymentHandler;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.userFacilities.Account;
import org.poo.utils.Operation;
import org.poo.utils.RequestSP;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SplitPayment extends Operation {
    private final ArrayList<User> waitingList = new ArrayList<>();
    private final ArrayList<RequestSP> requestsQueue = new ArrayList<>();

    public SplitPayment(final CommandHandler handler,
                        final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Executes the split payment
     */
    @Override
    public void execute() {
        if (createTransactionIfSplitInvalid()) {
            return;
        }

        double initialAmount = handler.getAmount();

        for (String account : handler.getAccounts()) {
            Account accountForSplit = DB.findAccountByIBAN(account);

            if (accountForSplit == null) {
                System.out.println("Account not found in execute");
                continue;
            }

            double amountToPay = getAmountForAccount(account, initialAmount);

            handler.setAmount(amountToPay);
            amountToPay = DB.convert(
                    amountToPay,
                    handler.getCurrency(),
                    accountForSplit.getCurrency());

            AccountHandler.removeFunds(accountForSplit, amountToPay);

            if (handler.getSplitPaymentType().equals("equal")) {
                addTransaction(
                        "Split payment of "
                                + String.format("%.2f", initialAmount)
                                + " " + handler.getCurrency(),
                        accountForSplit);
            } else if (handler.getSplitPaymentType().equals("custom")) {
                List<Double> amountForUsers = new ArrayList<>();
                for (String acc : handler.getAccounts()) {
                    amountForUsers.add(getAmountForAccount(acc, initialAmount));
                }

                addTransaction(
                        "Split payment of "
                            + String.format("%.2f", initialAmount)
                            + " " + handler.getCurrency(),
                        accountForSplit, amountForUsers);
            }
        }
    }

    /**
     * Add transaction to database
     * @param description transaction description
     * @param account account to add transaction to
     */
    private void addTransaction(final String description,
                                final Account account) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionSplitPaymentEqual(handler);
    }

    /**
     * Add transaction to database
     * @param description transaction description
     * @param account account to add transaction to
     * @param amountForUsers list of amounts for each user
     */
    private void addTransaction(final String description,
                                final Account account,
                                final List<Double> amountForUsers) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        handler.setAmountForUsers(amountForUsers);
        TransactionHandler.addTransactionSplitPaymentCustom(handler);
    }

    /**
     * Create transaction if split payment is invalid
     * @return true if split payment is invalid
     */
    private boolean createTransactionIfSplitInvalid() {
        Account accountWithoutFunds = searchForAccountWithoutFundsForSplit();

        double fullAmount = handler.getAmount();
        if (accountWithoutFunds != null) {
            if (handler.getSplitPaymentType().equals("equal")) {
                for (String acc : handler.getAccounts()) {
                    double amountToPay = getAmountForAccount(acc, fullAmount);
                    Account accForSplit = DB.findAccountByIBAN(acc);

                    if (accForSplit == null) {
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount)
                                    + " " + handler.getCurrency(),
                            "Account " + accountWithoutFunds.getIban()
                                    + " has insufficient funds for a split payment.",
                            accForSplit, amountToPay);
                }
            } else if (handler.getSplitPaymentType().equals("custom")) {
                List<Double> amountForUsers = new ArrayList<>();
                for (String acc : handler.getAccounts()) {
                    amountForUsers.add(getAmountForAccount(acc, fullAmount));
                }

                for (String acc : handler.getAccounts()) {
                    Account accForSplit = DB.findAccountByIBAN(acc);

                    if (accForSplit == null) {
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount)
                                    + " " + handler.getCurrency(),
                            "Account " + accountWithoutFunds.getIban()
                                    + " has insufficient funds for a split payment.",
                            accForSplit, amountForUsers);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Add transaction to database
     * @param description transaction description
     * @param errorMessage error message
     * @param account account to add transaction to
     * @param amount amount to add
     */
    private void addTransaction(final String description,
                               final String errorMessage,
                               final Account account,
                               final double amount) {
        handler.setDescription(description);
        handler.setEmail(account.getEmail());
        handler.setAccount(account.getIban());
        handler.setAmount(amount);
        handler.setErrorMessage(errorMessage);
        TransactionHandler.addTransactionErrorSplitPaymentEqual(handler);
    }

    /**
     * Add transaction to database
     * @param description transaction description
     * @param errorMessage error message
     * @param account account to add transaction to
     * @param amountForUsers list of amounts for each user
     */
    private void addTransaction(final String description,
                               final String errorMessage,
                               final Account account,
                               final List<Double> amountForUsers) {
        handler.setDescription(description);
        handler.setEmail(account.getEmail());
        handler.setAccount(account.getIban());
        handler.setAmountForUsers(amountForUsers);
        handler.setErrorMessage(errorMessage);
        TransactionHandler.addTransactionErrorSplitPaymentCustom(handler);
    }

    /**
     * Check for split payment validity
     * @param request request to check with
     */
    public void checkForSplitPayment(final RequestSP request) {
        if (!request.isAccepted()) {
            createTransactionRejected();

            // Cancel all requests for this split payment
            for (RequestSP req : requestsQueue) {
                req.setCancelled(true);
            }

            return;
        }

        requestsQueue.remove(request);

        if (requestsQueue.isEmpty()) {
            execute();
        }
    }

    /**
     * Create transaction if split payment is rejected
     */
    private void createTransactionRejected() {
        Account accountWithoutFunds = searchForAccountWithoutFundsForSplit();

        double fullAmount = handler.getAmount();
        if (accountWithoutFunds != null) {
            if (handler.getSplitPaymentType().equals("equal")) {
                for (String acc : handler.getAccounts()) {
                    double amountToPay = getAmountForAccount(acc, fullAmount);
                    Account accForSplit = DB.findAccountByIBAN(acc);

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount)
                                    + " " + handler.getCurrency(),
                            "One user rejected the payment.",
                            accForSplit, amountToPay);
                }
            } else if (handler.getSplitPaymentType().equals("custom")) {
                List<Double> amountForUsers = new ArrayList<>();
                for (String acc : handler.getAccounts()) {
                    amountForUsers.add(getAmountForAccount(acc, fullAmount));
                }

                for (String acc : handler.getAccounts()) {
                    Account accForSplit = DB.findAccountByIBAN(acc);

                    if (accForSplit == null) {
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount)
                                    + " " + handler.getCurrency(),
                            "One user rejected the payment.",
                            accForSplit, amountForUsers);
                }
            }
        }
    }

    /**
     * Search for account without funds for split payment
     * @return last account without funds or null if all accounts have funds
     */
    private Account searchForAccountWithoutFundsForSplit() {
        for (String account : handler.getAccounts()) {
            Account accountForSplit = DB.findAccountByIBAN(account);
            double amountToPay = getAmountForAccount(account, handler.getAmount());

            if (accountForSplit == null) {
                continue;
            }

            amountToPay = DB.convert(
                    amountToPay,
                    handler.getCurrency(),
                    accountForSplit.getCurrency());
            User user = DB.findUserByEmail(accountForSplit.getEmail());

            amountToPay = PaymentHandler.getAmountAfterFees(
                    user,
                    accountForSplit,
                    amountToPay);

            if (accountForSplit.getBalance() - amountToPay < 0) {
                return accountForSplit;
            }
        }
        return null;
    }

    /**
     * Send request notifications
     */
    public void sendRequestNotifications() {
        for (String account : handler.getAccounts()) {
            Account accountForSplit = DB.findAccountByIBAN(account);
            if (accountForSplit == null) {
                return;
            }

            User user = DB.findUserByEmail(accountForSplit.getEmail());
            sendRequestNotification(user);
        }
    }

    /**
     * Send request notification to user
     * @param user user to send request notification to
     */
    private void sendRequestNotification(final User user) {
        RequestSP request = new RequestSP(user, this);
        user.addRequestSP(request);
        requestsQueue.add(request);
        waitingList.add(user);
    }

    /**
     * Get the amount to pay for an account
     * @param account the account
     * @param fullAmount the full amount
     * @return the amount for the account
     */
    private double getAmountForAccount(final String account,
                                       final double fullAmount) {
        if (handler.getSplitPaymentType().equals("equal")) {
            return fullAmount / handler.getAccounts().size();
        } else if (handler.getSplitPaymentType().equals("custom")) {
            return handler.getAmountForUsers().get(handler.getAccounts().indexOf(account));
        }
        return 0;
    }

    /**
     * Get the type of the split payment
     * @return the type of the split payment
     */
    public String getType() {
        return handler.getSplitPaymentType();
    }
}
