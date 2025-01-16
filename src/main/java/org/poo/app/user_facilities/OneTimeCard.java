package org.poo.app.user_facilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.utils.CardVisitor;

public class OneTimeCard extends Card {

    public OneTimeCard(final String currency, final String iban, final String email) {
        super(currency, iban, email);
        this.setType("one-time");
    }

    /**
     * Visitor pattern accept method
     * @param visitor CardVisitor object
     * @return the ObjectNode created by the visitor
     */
    @Override
    public ObjectNode accept(final CardVisitor visitor) {
        return visitor.visit(this);
    }
}
