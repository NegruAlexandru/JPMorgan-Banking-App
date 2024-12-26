package org.poo.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AccountInterface {
    /**
     * Visitor pattern accept method
     * @param visitor AccountVisitor object
     * @return the ObjectNode created by the visitor
     */
    ObjectNode accept(AccountVisitor visitor);
}
