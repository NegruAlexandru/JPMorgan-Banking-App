package org.poo.app.userFacilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.utils.AccountVisitor;

@Data
@NoArgsConstructor
public class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final String email, final String currency, final double interestRate) {
        super(email, currency);
        this.interestRate = interestRate;
        this.setType("savings");
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
