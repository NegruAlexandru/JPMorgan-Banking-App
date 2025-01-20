package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.*;
import org.poo.app.user_facilities.Account;
import org.poo.utils.Operation;
import org.poo.utils.RequestSP;

import java.util.ArrayList;
import java.util.List;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

public class SplitPayment extends Operation {
    ArrayList<User> waitingList;
    ArrayList<RequestSP> requestsQueue;

    public SplitPayment(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
        waitingList = new ArrayList<>();
        requestsQueue = new ArrayList<>();
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
            amountToPay = DB.convert(amountToPay,
                    handler.getCurrency(), accountForSplit.getCurrency());

            if (accountForSplit.getIban().equals(ibannenorocit)) {
                System.out.println("From split payment: " + amountToPay);
            }

            AccountHandler.removeFunds(accountForSplit, amountToPay);

            if (handler.getSplitPaymentType().equals("equal")) {
                addTransaction("Split payment of "
                        + String.format("%.2f", initialAmount) + " " + handler.getCurrency(),
                        accountForSplit);
            } else if (handler.getSplitPaymentType().equals("custom")) {
                List<Double> amountForUsers = new ArrayList<>();
                for (String acc : handler.getAccounts()) {
                    amountForUsers.add(getAmountForAccount(acc, initialAmount));
                }

                addTransaction("Split payment of "
                        + String.format("%.2f", initialAmount) + " " + handler.getCurrency(),
                        accountForSplit, amountForUsers);
            }
        }
    }

    private boolean createTransactionRejected() {
        Account accountWithoutFunds = searchForAccountWithoutFundsForSplit();

        double fullAmount = handler.getAmount();
        if (accountWithoutFunds != null) {
            if (handler.getSplitPaymentType().equals("equal")) {
                for (String acc : handler.getAccounts()) {
                    double amountToPay = getAmountForAccount(acc, fullAmount);
                    Account accForSplit = DB.findAccountByIBAN(acc);

                    if (accForSplit == null) {
                        System.out.println("Account not found in createTransactionIfSplitInvalid");
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount) + " " + handler.getCurrency(),
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
                        System.out.println("Account not found in createTransactionIfSplitInvalid");
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount) + " " + handler.getCurrency(),
                            "One user rejected the payment.",
                            accForSplit, amountForUsers);
                }
            }

            return true;
        }

        return false;
    }

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

    public void sendRequestNotification(final User user) {
        RequestSP request = new RequestSP(user, this);
        user.addRequestSP(request);
        requestsQueue.add(request);
        waitingList.add(user);
    }

    public void addTransaction(final String description, final Account account) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        TransactionHandler.addTransactionSplitPaymentEqual(handler);
    }

    public void addTransaction(final String description, final Account account, final List<Double> amountForUsers) {
        handler.setEmail(account.getEmail());
        handler.setDescription(description);
        handler.setAccount(account.getIban());
        handler.setAmountForUsers(amountForUsers);
        TransactionHandler.addTransactionSplitPaymentCustom(handler);
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

            amountToPay = DB.convert(amountToPay, handler.getCurrency(), accountForSplit.getCurrency());
            User user = DB.findUserByEmail(accountForSplit.getEmail());

            amountToPay = PaymentHandler.getAmountAfterFees(user, accountForSplit, amountToPay);

            if (accountForSplit.getBalance() - amountToPay < 0) {
                return accountForSplit;
            }
        }
        return null;
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
                        System.out.println("Account not found in createTransactionIfSplitInvalid");
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount) + " " + handler.getCurrency(),
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
                        System.out.println("Account not found in createTransactionIfSplitInvalid");
                        continue;
                    }

                    addTransaction("Split payment of "
                                    + String.format("%.2f", fullAmount) + " " + handler.getCurrency(),
                            "Account " + accountWithoutFunds.getIban()
                                    + " has insufficient funds for a split payment.",
                            accForSplit, amountForUsers);
                }
            }

            return true;
        }

        return false;
    }

    public void addTransaction(final String description, final String errorMessage,
                               final Account account, final double amount) {
        handler.setDescription(description);
        handler.setEmail(account.getEmail());
        handler.setAccount(account.getIban());
        handler.setAmount(amount);
        handler.setErrorMessage(errorMessage);
        TransactionHandler.addTransactionErrorSplitPaymentEqual(handler);
    }

    public void addTransaction(final String description, final String errorMessage,
                               final Account account, final List<Double> amountForUsers) {
        handler.setDescription(description);
        handler.setEmail(account.getEmail());
        handler.setAccount(account.getIban());
        handler.setAmountForUsers(amountForUsers);
        handler.setErrorMessage(errorMessage);
        TransactionHandler.addTransactionErrorSplitPaymentCustom(handler);
    }

    public double getAmountForAccount(String account, double fullAmount) {
        if (handler.getSplitPaymentType().equals("equal")) {
            return fullAmount / handler.getAccounts().size();
        } else if (handler.getSplitPaymentType().equals("custom")) {
            return handler.getAmountForUsers().get(handler.getAccounts().indexOf(account));
        }
        return 0;
    }

    public String getType() {
        return handler.getSplitPaymentType();
    }
}
