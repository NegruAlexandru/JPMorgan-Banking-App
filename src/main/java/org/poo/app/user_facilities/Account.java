package org.poo.app.user_facilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.app.logic_handlers.DB;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.utils.AccountInterface;
import org.poo.utils.AccountVisitor;
import org.poo.utils.Utils;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Account implements AccountInterface {
    private String email;
    private String currency;
    private String iban;
    private double balance;
    private double minBalance;
    private String type;
    private ArrayList<Card> cards;
    private ArrayList<Transaction> transactions;

    public Account(final String email, final String currency) {
        this.email = email;
        this.currency = currency;
        this.iban = Utils.generateIBAN();
        this.minBalance = 0;
        this.balance = 0;
        this.type = "classic";
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    /**
     * Create a card for the account
     * @return the card number
     */
    public String createCard() {
        Card card = new Card(this.getCurrency(), this.getIban());
        this.getCards().add(card);
        DB.getAccountsWithCardNumber().put(card.getCardNumber(), this);
        return card.getCardNumber();
    }

    /**
     * Create a one-time card for the account
     * @return the card number
     */
    public String createOneTimeCard() {
        Card card = new OneTimeCard(this.getCurrency(), this.getIban());
        this.getCards().add(card);
        DB.addAccountWithCardNumber(this, card.getCardNumber());
        return card.getCardNumber();
    }

    /**
     * Delete a card from the account
     * @param cardNumber card number to delete
     */
    public void deleteCard(final String cardNumber) {
        for (Card c : cards) {
            if (c.getCardNumber().equals(cardNumber)) {
                cards.remove(c);
                DB.removeAccountWithCardNumber(cardNumber);
                return;
            }
        }
    }

    /**
     * Delete all cards from the account
     */
    public void deleteAllCards() {
        for (Card c : cards) {
            cards.remove(c);
            DB.removeAccountWithCardNumber(c.getCardNumber());
        }
    }

    /**
     * Visitor pattern accept method
     * @param visitor AccountVisitor object
     * @return the ObjectNode created by the visitor
     */
    @Override
    public ObjectNode accept(final AccountVisitor visitor) {
        return visitor.visit(this);
    }
}
