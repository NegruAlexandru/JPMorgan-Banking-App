package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.input.ExchangeRate;
import org.poo.app.logic_handlers.*;
import org.poo.app.input.User;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.app.user_facilities.Card;
import org.poo.app.user_facilities.OneTimeCard;
import org.poo.utils.Operation;

import static org.poo.app.logic_handlers.CommandHandler.ibannenorocit;

public class CashWithdrawal extends Operation {
    public CashWithdrawal(CommandHandler handler, ArrayNode output) {
        super(handler, output);
    }

    /**
     * Withdraw money from an account
     */
    @Override
    public void execute() {
        Card card = DB.getCardByCardNumber(handler.getCardNumber());

        if (card == null || card.getCardStatus().equals("frozen")) {
            //Card not found
            addTransactionToOutput("description", "Card not found");
            return;
        }




        Account account = DB.findAccountByCardNumber(handler.getCardNumber());

        if (account == null) {
            //Account not found

            return;
        }
        if (account.getIban().equals(ibannenorocit)) {
            System.out.println("card number: " + handler.getCardNumber());
            System.out.println("card here: " + card.getCardNumber());
        }

//        if (card.getCardNumber().equals("8916302128573242")) {
//            System.out.println("account iban: " + account.getIban());
//            return;
//        }

        handler.setAccount(account.getIban());

        User user = DB.findUserByEmail(handler.getEmail());
        if (user == null) {
            //User not found
//            addTransactionToDB("User not found");
            addTransactionToOutput("description", "User not found");
            return;
        }

        double amount = DB.convert(handler.getAmount(), "RON", account.getCurrency());
        amount = PaymentHandler.getAmountAfterFees(user, account, amount);

        if (account.getBalance() < amount) {
            //Insufficient funds
            super.addTransactionToDB("Insufficient funds");
            return;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.getManagers().contains(user) && !businessAccount.getOwner().equals(user)
            && businessAccount.getEmployees().contains(user)) {
                if (amount < businessAccount.getSpendingLimit()) {
//                    super.addTransactionToDB("Amount is less than the withdraw limit");
                    return;
                }
            }
        }

        addTransactionToDB("Cash withdrawal of " + handler.getAmount());

        AccountHandler.removeFunds(account, amount);
//????????????????????????????????????????????
        if (card.getType().equals("one-time")) {
            if (account.getBalance() < 0) {

//            new DeleteCard(handler, null).execute();
//            new CreateOneTimeCard(handler, null).execute();
                account.getCards().remove(card);
                account.getCards().add(new OneTimeCard(account.getCurrency(), account.getIban(), account.getEmail()));
            }
        }
    }

    public void addTransactionToDB(final String description) {
        handler.setDescription(description);
        TransactionHandler.cashWithdrawalTransactionToDB(handler);
    }
}
