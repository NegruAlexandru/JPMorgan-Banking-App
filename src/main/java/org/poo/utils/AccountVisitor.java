package org.poo.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.SavingsAccount;

public interface AccountVisitor {
    /**
     * Visitor pattern visit method for Account
     * @param account account to visit
     * @return ObjectNode with the account's information
     */
    ObjectNode visit(Account account);
    /**
     * Visitor pattern visit method for SavingsAccount
     * @param savingsAccount savings account to visit
     * @return ObjectNode with the account's information
     */
    ObjectNode visit(SavingsAccount savingsAccount);
}
