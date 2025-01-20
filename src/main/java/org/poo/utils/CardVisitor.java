package org.poo.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.userFacilities.Card;
import org.poo.app.userFacilities.OneTimeCard;

public interface CardVisitor {
    /**
     * Visitor pattern visit method for Card
     * @param card card to visit
     * @return ObjectNode with the account's information
     */
    ObjectNode visit(Card card);
    /**
     * Visitor pattern visit method for OneTimeCard
     * @param card card to visit
     * @return ObjectNode with the account's information
     */
    ObjectNode visit(OneTimeCard card);
}
