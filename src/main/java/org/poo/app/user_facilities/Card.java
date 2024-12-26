package org.poo.app.user_facilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.utils.CardInterface;
import org.poo.utils.CardVisitor;
import org.poo.utils.Utils;

@Data
@NoArgsConstructor
public class Card implements CardInterface {
    private String cardNumber;
    private String currency;
    private String cardStatus;
    private String iban;
    private String type;

    public Card(final String currency, final String iban) {
        this.cardNumber = Utils.generateCardNumber();
        this.currency = currency;
        this.iban = iban;
        this.cardStatus = "active";
        this.type = "classic";
    }

    /**
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
