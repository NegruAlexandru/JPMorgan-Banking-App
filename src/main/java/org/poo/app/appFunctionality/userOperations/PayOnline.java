package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.Commerciant;
import org.poo.app.input.User;
import org.poo.app.logicHandlers.*;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.app.userFacilities.Card;
import org.poo.utils.Operation;

public class PayOnline extends Operation {
    public PayOnline(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }
    /**
     * Pay online with a card
     */
    @Override
    public void execute() {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());

        if (card == null) {
            addTransactionToOutput("description", "Card not found");
            return;
        }

        if (handler.getAmount() <= 0) {
            return;
        }

        Account ownerAccount = DB.findAccountByCardNumber(handler.getCardNumber());

        if (ownerAccount == null) {
            return;
        }

        User requestor = DB.findUserByEmail(handler.getEmail());

        if (ownerAccount.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) ownerAccount;
            if (!businessAccount.getEmployees().contains(requestor)
                && !businessAccount.getManagers().contains(requestor)
                && !businessAccount.getOwner().equals(requestor)) {
                addTransactionToOutput("description", "Card not found");
                return;
            }

            if (!businessAccount.getOwner().equals(requestor)) {
                if (handler.getAmount() > businessAccount.getSpendingLimit()) {
                    return;
                }
            }
        } else {
            if (!ownerAccount.getEmail().equals(handler.getEmail())) {
                addTransactionToOutput("description", "Card not found");
                return;
            }
        }

        handler.setAmount(DB.convert(
                handler.getAmount(),
                handler.getCurrency(),
                ownerAccount.getCurrency()));

        User user = DB.findUserByEmail(ownerAccount.getEmail());

        Commerciant commerciant = DB.getCommerciantByName(handler.getCommerciant());
        if (commerciant == null) {
            return;
        }

        double amountAfterFees = PaymentHandler.getAmountAfterFees(
                user,
                ownerAccount,
                handler.getAmount());

        handler.setAccount(ownerAccount.getIban());
        if (ownerAccount.getBalance() - amountAfterFees < 0) {
            handler.setDescription("Insufficient funds");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);

            return;
        } else if (ownerAccount.getBalance() - ownerAccount.getMinBalance() - amountAfterFees <= 0) {
            handler.setDescription("The card is frozen");
            card.setCardStatus("frozen");
            TransactionHandler.addTransactionDescriptionTimestamp(handler);

            return;
        }

        PaymentHandler.pay(ownerAccount, card, handler);
    }
}
