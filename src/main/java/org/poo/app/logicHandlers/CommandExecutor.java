package org.poo.app.logicHandlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import org.poo.app.appFunctionality.debug.PrintTransactions;
import org.poo.app.appFunctionality.debug.PrintUsers;
import org.poo.app.appFunctionality.userOperations.*;

@Getter
public class CommandExecutor {
    public void addAccount(CommandHandler handler, ArrayNode output) {
        new AddAccount(handler, output).execute();
    }

    public void addFunds(CommandHandler handler, ArrayNode output) {
        new AddFunds(handler, output).execute();
    }

    public void cashWithdrawal(CommandHandler handler, ArrayNode output) {
        new CashWithdrawal(handler, output).execute();
    }

    public void withdrawSavings(CommandHandler handler, ArrayNode output) {
        new WithdrawSavings(handler, output).execute();
    }

    public void addInterest(CommandHandler handler, ArrayNode output) {
        new AddInterest(handler, output).execute();
    }

    public void changeInterestRate(CommandHandler handler, ArrayNode output) {
        new ChangeInterestRate(handler, output).execute();
    }

    public void checkCardStatus(CommandHandler handler, ArrayNode output) {
        new CheckCardStatus(handler, output).execute();
    }

    public void createCard(CommandHandler handler, ArrayNode output) {
        new CreateCard(handler, output).execute();
    }

    public void createOneTimeCard(CommandHandler handler, ArrayNode output) {
        new CreateOneTimeCard(handler, output).execute();
    }

    public void deleteCard(CommandHandler handler, ArrayNode output) {
        new DeleteCard(handler, output).execute();
    }

    public void deleteAccount(CommandHandler handler, ArrayNode output) {
        new DeleteAccount(handler, output).execute();
    }

    public void payOnline(CommandHandler handler, ArrayNode output) {
        new PayOnline(handler, output).execute();
    }

    public void report(CommandHandler handler, ArrayNode output) {
        new Report(handler, output).execute();
    }

    public void sendMoney(CommandHandler handler, ArrayNode output) {
        new SendMoney(handler, output).execute();
    }

    public void setAlias(CommandHandler handler, ArrayNode output) {
        new SetAlias(handler, output).execute();
    }

    public void setMinimumBalance(CommandHandler handler, ArrayNode output) {
        new SetMinimumBalance(handler, output).execute();
    }

    public void splitPayment(CommandHandler handler, ArrayNode output) {
        new SplitPayment(handler, output).sendRequestNotifications();
    }

    public void spendingsReport(CommandHandler handler, ArrayNode output) {
        new SpendingsReport(handler, output).execute();
    }

    public void printUsers(CommandHandler handler, ArrayNode output) {
        new PrintUsers(handler, output).execute();
    }

    public void printTransactions(CommandHandler handler, ArrayNode output) {
        new PrintTransactions(handler, output).execute();
    }

    public void upgradePlan(CommandHandler handler, ArrayNode output) {
        new UpgradePlan(handler, output).execute();
    }

    public void acceptSplitPayment(CommandHandler handler, ArrayNode output) {
        new AcceptSplitPayment(handler, output).execute();
    }

    public void rejectSplitPayment(CommandHandler handler, ArrayNode output) {
        new RejectSplitPayment(handler, output).execute();
    }

    public void addNewBusinessAssociate(CommandHandler handler, ArrayNode output) {
        new AddNewBusinessAssociate(handler, output).execute();
    }

    public void changeDepositLimit(CommandHandler handler, ArrayNode output) {
        new ChangeDepositLimit(handler, output).execute();
    }

    public void changeSpendingLimit(CommandHandler handler, ArrayNode output) {
        new ChangeSpendingLimit(handler, output).execute();
    }

    public void businessReport(CommandHandler handler, ArrayNode output) {
        new BusinessReport(handler, output).execute();
    }
}
