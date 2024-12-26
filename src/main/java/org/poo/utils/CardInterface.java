package org.poo.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface CardInterface {
    /**
     * Visitor pattern accept method
     * @param visitor CardVisitor object
     * @return the ObjectNode created by the visitor
     */
    ObjectNode accept(CardVisitor visitor);
}
