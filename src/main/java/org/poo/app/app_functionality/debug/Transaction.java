package org.poo.app.app_functionality.debug;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;

import java.util.List;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

@Data
@NoArgsConstructor
public final class Transaction {
    private int timestamp;
    private String description;
    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;
    private List<String> involvedAccounts;
    private String account;
    private String card;
    private String cardHolder;
    private String commerciant;
    private Double amountDouble;
    private String currency;
    private String errorMessage;
    private String classicAccountIBAN;
    private String savingsAccountIBAN;
    private User businessAssociatedUser;
    private String newPlanType;
    private String accountIBAN;

    private Transaction(final Builder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.senderIBAN = builder.senderIBAN;
        this.receiverIBAN = builder.receiverIBAN;
        this.amount = builder.amount;
        this.transferType = builder.transferType;
        this.involvedAccounts = builder.involvedAccounts;
        this.account = builder.account;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.commerciant = builder.commerciant;
        this.amountDouble = builder.amountDouble;
        this.currency = builder.currency;
        this.errorMessage = builder.errorMessage;
        this.classicAccountIBAN = builder.classicAccountIBAN;
        this.savingsAccountIBAN = builder.savingsAccountIBAN;
        this.businessAssociatedUser = builder.businessAssociatedUser;
        this.newPlanType = builder.newPlanType;
        this.accountIBAN = builder.accountIBAN;
    }

    public static final class Builder {
        private final int timestamp;
        private final String description;
        private String senderIBAN;
        private String receiverIBAN;
        private String amount;
        private String transferType;
        private List<String> involvedAccounts;
        private String account;
        private String card;
        private String cardHolder;
        private String commerciant;
        private Double amountDouble = (double) 0;
        private String currency;
        private String errorMessage;
        private String classicAccountIBAN;
        private String savingsAccountIBAN;
        private User businessAssociatedUser;
        private String newPlanType;
        private String accountIBAN;

        public Builder(final int timestamp, final String description) {
            this.timestamp = timestamp;
            this.description = description;
        }

        /**
         * Sets the senderIBAN of the transaction
         *
         * @param senderIBAN the senderIBAN of the transaction
         * @return this Builder instance
         */
        public Builder senderIBAN(final String senderIBAN) {
            this.senderIBAN = senderIBAN;
            return this;
        }

        /**
         * Sets the receiverIBAN of the transaction
         *
         * @param receiverIBAN the receiverIBAN of the transaction
         * @return this Builder instance
         */
        public Builder receiverIBAN(final String receiverIBAN) {
            this.receiverIBAN = receiverIBAN;
            return this;
        }

        /**
         * Sets the amount of the transaction
         *
         * @param amount the amount of the transaction
         * @return this Builder instance
         */
        public Builder amount(final String amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Sets the transferType of the transaction
         *
         * @param transferType the transferType of the transaction
         * @return this Builder instance
         */
        public Builder transferType(final String transferType) {
            this.transferType = transferType;
            return this;
        }

        /**
         * Sets the involvedAccounts of the transaction
         *
         * @param involvedAccounts the involvedAccounts of the transaction
         * @return this Builder instance
         */
        public Builder involvedAccounts(final List<String> involvedAccounts) {
            this.involvedAccounts = involvedAccounts;
            return this;
        }

        /**
         * Sets the account of the transaction
         *
         * @param account the account of the transaction
         * @return this Builder instance
         */
        public Builder account(final String account) {
            this.account = account;
            return this;
        }

        /**
         * Sets the card of the transaction
         *
         * @param card the card of the transaction
         * @return this Builder instance
         */
        public Builder card(final String card) {
            this.card = card;
            return this;
        }

        /**
         * Sets the cardHolder of the transaction
         *
         * @param cardHolder the cardHolder of the transaction
         * @return this Builder instance
         */
        public Builder cardHolder(final String cardHolder) {
            this.cardHolder = cardHolder;
            return this;
        }

        /**
         * Sets the commerciant of the transaction
         *
         * @param commerciant the commerciant of the transaction
         * @return this Builder instance
         */
        public Builder commerciant(final String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        /**
         * Sets the amountDouble of the transaction
         *
         * @param amountDouble the amountDouble of the transaction
         * @return this Builder instance
         */
        public Builder amountDouble(final Double amountDouble) {
            this.amountDouble = amountDouble;
            return this;
        }

        /**
         * Sets the currency of the transaction
         *
         * @param currency the currency of the transaction
         * @return this Builder instance
         */
        public Builder currency(final String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Sets the errorMessage of the transaction
         *
         * @param errorMessage the errorMessage of the transaction
         * @return this Builder instance
         */
        public Builder errorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        /**
         * Sets the classicAccountIBAN of the transaction
         *
         * @param classicAccountIBAN the classicAccountIBAN of the transaction
         * @return this Builder instance
         */
        public Builder classicAccountIBAN(final String classicAccountIBAN) {
            this.classicAccountIBAN = classicAccountIBAN;
            return this;
        }

        /**
         * Sets the savingsAccountIBAN of the transaction
         *
         * @param savingsAccountIBAN the savingsAccountIBAN of the transaction
         * @return this Builder instance
         */
        public Builder savingsAccountIBAN(final String savingsAccountIBAN) {
            this.savingsAccountIBAN = savingsAccountIBAN;
            return this;
        }

        public Builder businessAssociatedUser(final User businessAssociatedUser) {
            this.businessAssociatedUser = businessAssociatedUser;
            return this;
        }

        public Builder newPlanType(final String newPlanType) {
            this.newPlanType = newPlanType;
            return this;
        }

        public Builder accountIBAN(final String accountIBAN) {
            this.accountIBAN = accountIBAN;
            return this;
        }

        /**
         * Builds the Transaction object
         *
         * @return the Transaction object
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    /**
     * Adds a transaction to an account
     * @param account account to add the transaction to
     */
    public void addTransaction(final Account account) {
        account.getTransactions().add(this);
    }

    public void addBusinessTransaction(final BusinessAccount account) {
        account.getBusinessTransactions().add(this);
    }

    /**
     * Formats the output of a transaction to an ObjectNode
     * @param transaction transaction to format
     * @return ObjectNode with the formatted transaction
     */

    public static ObjectNode formatOutput(final Transaction transaction) {
        ObjectNode transactionNode = OBJECT_MAPPER.createObjectNode();
        if (transaction.getAccount() != null) {
            transactionNode.put("account", transaction.getAccount());
        }

        if (transaction.getCard() != null) {
            transactionNode.put("card", transaction.getCard());
        }

        if (transaction.getCardHolder() != null) {
            transactionNode.put("cardHolder", transaction.getCardHolder());
        }

        if (transaction.getAmount() != null) {
            transactionNode.put("amount", transaction.getAmount());
        }

        if (transaction.getAmountDouble() != 0) {
            transactionNode.put("amount", transaction.getAmountDouble());
        }

        if (transaction.getCurrency() != null) {
            transactionNode.put("currency", transaction.getCurrency());
        }

        if (transaction.getCommerciant() != null) {
            transactionNode.put("commerciant", transaction.getCommerciant());
        }

        if (transaction.getDescription() != null) {
            transactionNode.put("description", transaction.getDescription());
        }

        if (transaction.getErrorMessage() != null) {
            transactionNode.put("error", transaction.getErrorMessage());
        }

        if (transaction.getInvolvedAccounts() != null) {
            ArrayNode accounts = OBJECT_MAPPER.createArrayNode();
            for (String acc : transaction.getInvolvedAccounts()) {
                accounts.add(acc);
            }
            transactionNode.set("involvedAccounts", accounts);
        }

        if (transaction.getReceiverIBAN() != null) {
            transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
        }

        if (transaction.getSenderIBAN() != null) {
            transactionNode.put("senderIBAN", transaction.getSenderIBAN());
        }

        if (transaction.getTimestamp() != 0) {
            transactionNode.put("timestamp", transaction.getTimestamp());
        }

        if (transaction.getTransferType() != null) {
            transactionNode.put("transferType", transaction.getTransferType());
        }

        if (transaction.getClassicAccountIBAN() != null) {
            transactionNode.put("classicAccountIBAN", transaction.getClassicAccountIBAN());
        }

        if (transaction.getSavingsAccountIBAN() != null) {
            transactionNode.put("savingsAccountIBAN", transaction.getSavingsAccountIBAN());
        }

        if (transaction.getBusinessAssociatedUser() != null) {
            transactionNode.put("businessAssociatedUser", transaction.getBusinessAssociatedUser().getEmail());
        }

        if (transaction.getNewPlanType() != null) {
            transactionNode.put("newPlanType", transaction.getNewPlanType());
        }

        if (transaction.getAccountIBAN() != null) {
            transactionNode.put("accountIBAN", transaction.getAccountIBAN());
        }

        return transactionNode;
    }
}
