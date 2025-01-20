package org.poo.app.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.baseClasses.User;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.logicHandlers.TransactionHandler;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.app.userFacilities.Card;
import org.poo.app.userFacilities.SavingsAccount;
import org.poo.app.userFacilities.OneTimeCard;
import org.poo.utils.AccountVisitor;
import org.poo.utils.CardVisitor;

public class AccountHandler implements AccountVisitor, CardVisitor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int NR_OF_TRANSACTIONS_FOR_PLAN_UPGRADE = 5;
    private static final int PLAN_UPGRADE_THRESHOLD = 300;
    private static final String THRESHOLD_CURRENCY = "RON";
    private static final String PLAN_TO_UPGRADE_FROM = "silver";
    private static final String PLAN_TO_UPGRADE_TO = "gold";

    /**
     * Removes funds from an account
     * @param account account to add funds to
     * @param amount amount to remove
     */
    public static void addFunds(final Account account,
                                final double amount) {
        account.setBalance(account.getBalance() + amount);
    }

    /**
     * Removes funds from an account
     * @param account account to remove funds from
     * @param amount amount to remove
     */
    public static void removeFunds(final Account account,
                                   final double amount) {
        account.setBalance(account.getBalance() - amount);
    }

    /**
     * Transfers funds between two accounts
     * @param sender account to remove funds from
     * @param receiver account to add funds to
     * @param amount amount to transfer
     * @return the amount transferred
     */
    public static double transferFunds(final Account sender,
                                       final Account receiver,
                                       final double amount) {

        User user = DB.findUserByEmail(sender.getEmail());
        double amountAndFees = PaymentHandler.getAmountAfterFees(
                user,
                sender,
                amount);

        removeFunds(sender, amountAndFees);

        double amountReceived = DB.convert(
                amount,
                sender.getCurrency(),
                receiver.getCurrency());

        addFunds(receiver, amountReceived);

        return amountReceived;
    }

    /**
     * Increments the number of transactions for a user and checks if a plan upgrade is available
     * @param user user to increment the number of transactions for
     * @param account account to check the number of transactions for
     * @param amount amount to check if a plan upgrade is available
     * @param handler command handler to set the new plan type
     */
    public static void incrementAndCheckIfPlanUpgradeIsAvailable(final User user,
                                                                 final Account account,
                                                                 final double amount,
                                                                 final CommandHandler handler) {
        if (user.getPlan().equals(PLAN_TO_UPGRADE_FROM)) {
            double amountInCurrencyNeeded = DB.convert(
                    amount,
                    account.getCurrency(),
                    THRESHOLD_CURRENCY);
            if (amountInCurrencyNeeded >= PLAN_UPGRADE_THRESHOLD) {
                int num = user.getNrOfTransactionsEligibleForPlanUpgrade();
                num++;
                user.setNrOfTransactionsEligibleForPlanUpgrade(num);

                if (num == NR_OF_TRANSACTIONS_FOR_PLAN_UPGRADE) {
                    user.setPlan(PLAN_TO_UPGRADE_TO);
                    handler.setNewPlanType(user.getPlan());
                    handler.setAccount(account.getIban());
                    handler.setDescription("Upgrade plan");
                    TransactionHandler.addUpgradePlanTransactionToDB(handler);
                }
            }
        }
    }

    /**
     * Adds a user to a business account's managers list
     * @param account business account to add user to
     * @param user user to add to the business account's managers list
     */
    public void addNewManager(final BusinessAccount account,
                              final User user) {
        account.getManagers().add(user);
    }

    /**
     * Adds a user to a business account's employees list
     * @param account business account to add user to
     * @param user user to add to the business account's employees list
     */
    public void addNewEmployee(final BusinessAccount account,
                               final User user) {
        account.getEmployees().add(user);
    }

    /**
     * Creates a JSON object for a user
     * @param user user to create JSON object for
     * @return JSON object for the user
     */
    public ObjectNode visit(final User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userNode = objectMapper.createObjectNode();

        userNode.put("firstName", user.getFirstName());
        userNode.put("lastName", user.getLastName());
        userNode.put("email", user.getEmail());
        ArrayNode accounts = objectMapper.createArrayNode();
        for (Account account : user.getAccounts()) {
            accounts.add(account.accept(this));
        }
        userNode.set("accounts", accounts);

        return userNode;
    }

    /**
     * Creates a JSON object for a classic account
     * @param account classic account to create JSON object for
     * @return JSON object for the classic account
     */
    @Override
    public ObjectNode visit(final Account account) {
        ObjectNode accountNode = OBJECT_MAPPER.createObjectNode();

        ArrayNode cardsNode = OBJECT_MAPPER.createArrayNode();
        for (Card c : account.getCards()) {
            cardsNode.add(c.accept(this));
        }

        accountNode.put("IBAN", account.getIban());
        accountNode.put("balance", account.getBalance());
        accountNode.put("currency", account.getCurrency());
        accountNode.put("type", account.getType());
        accountNode.set("cards", cardsNode);
        return accountNode;
    }

    /**
     * Creates a JSON object for a savings account
     * @param account savings account to create JSON object for
     * @return JSON object for the savings account
     */
    @Override
    public ObjectNode visit(final SavingsAccount account) {
        ObjectNode accountNode = OBJECT_MAPPER.createObjectNode();

        ArrayNode cardsNode = OBJECT_MAPPER.createArrayNode();
        for (Card c : account.getCards()) {
            cardsNode.add(c.accept(this));
        }

        accountNode.put("IBAN", account.getIban());
        accountNode.put("balance", account.getBalance());
        accountNode.put("currency", account.getCurrency());
        accountNode.put("type", account.getType());
        accountNode.set("cards", cardsNode);
        return accountNode;
    }

    /**
     * Creates a JSON object for a business account
     * @param account business account to create JSON object for
     * @return JSON object for the business account
     */
    @Override
    public ObjectNode visit(final BusinessAccount account) {
        ObjectNode accountNode = OBJECT_MAPPER.createObjectNode();

        ArrayNode cardsNode = OBJECT_MAPPER.createArrayNode();
        for (Card c : account.getCards()) {
            cardsNode.add(c.accept(this));
        }

        accountNode.put("IBAN", account.getIban());
        accountNode.put("balance", account.getBalance());
        accountNode.put("currency", account.getCurrency());
        accountNode.put("type", account.getType());
        accountNode.set("cards", cardsNode);
        return accountNode;
    }

    /**
     * Creates a JSON object for a card
     * @param card card to create JSON object for
     * @return JSON object for the card
     */
    @Override
    public ObjectNode visit(final Card card) {
        ObjectNode cardNode = OBJECT_MAPPER.createObjectNode();
        cardNode.put("cardNumber", card.getCardNumber());
        cardNode.put("status", card.getCardStatus());

        return cardNode;
    }

    /**
     * Creates a JSON object for a one-time card
     * @param card one-time card to create JSON object for
     * @return JSON object for the one-time card
     */
    @Override
    public ObjectNode visit(final OneTimeCard card) {
        ObjectNode cardNode = OBJECT_MAPPER.createObjectNode();
        cardNode.put("cardNumber", card.getCardNumber());
        cardNode.put("status", card.getCardStatus());

        return cardNode;
    }
}
