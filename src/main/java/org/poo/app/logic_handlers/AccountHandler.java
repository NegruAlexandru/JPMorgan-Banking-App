package org.poo.app.logic_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.OneTimeCard;
import org.poo.app.user_facilities.SavingsAccount;
import org.poo.utils.AccountVisitor;
import org.poo.utils.CardVisitor;

public class AccountHandler implements AccountVisitor, CardVisitor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Adds funds to an account
     * @param iban IBAN of the account
     * @param amount amount to add
     */
    public static void addFunds(final String iban, final double amount) {
        Account account = DB.findAccountByIBAN(iban);
        if (account != null) {
            account.setBalance(account.getBalance() + amount);
        }
    }

    /**
     * Removes funds from an account
     * @param account account to add funds to
     * @param amount amount to remove
     */
    public static void addFunds(final Account account, final double amount) {
        account.setBalance(account.getBalance() + amount);
    }

    /**
     * Removes funds from an account
     * @param account account to remove funds from
     * @param amount amount to remove
     */
    public static void removeFunds(final Account account, final double amount) {
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
                                       final Account receiver, double amount) {
        removeFunds(sender, amount);

        ExchangeRate exchangeRate = DB.getExchangeRate(sender.getCurrency(),
                receiver.getCurrency());
        amount *= exchangeRate.getRate();

        addFunds(receiver, amount);

        return amount;
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
     * Creates a JSON object for an account
     * @param account account to create JSON object for
     * @return JSON object for the account
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
        accountNode.put("type", "classic");
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
        accountNode.put("type", "savings");
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
