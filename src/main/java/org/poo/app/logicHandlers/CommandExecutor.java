package org.poo.app.logicHandlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import org.poo.app.appFunctionality.debug.PrintTransactions;
import org.poo.app.appFunctionality.debug.PrintUsers;
import org.poo.app.appFunctionality.userOperations.AcceptSplitPayment;
import org.poo.app.appFunctionality.userOperations.AddAccount;
import org.poo.app.appFunctionality.userOperations.AddFunds;
import org.poo.app.appFunctionality.userOperations.AddInterest;
import org.poo.app.appFunctionality.userOperations.AddNewBusinessAssociate;
import org.poo.app.appFunctionality.userOperations.BusinessReport;
import org.poo.app.appFunctionality.userOperations.CashWithdrawal;
import org.poo.app.appFunctionality.userOperations.ChangeDepositLimit;
import org.poo.app.appFunctionality.userOperations.ChangeInterestRate;
import org.poo.app.appFunctionality.userOperations.ChangeSpendingLimit;
import org.poo.app.appFunctionality.userOperations.CheckCardStatus;
import org.poo.app.appFunctionality.userOperations.CreateCard;
import org.poo.app.appFunctionality.userOperations.CreateOneTimeCard;
import org.poo.app.appFunctionality.userOperations.DeleteAccount;
import org.poo.app.appFunctionality.userOperations.DeleteCard;
import org.poo.app.appFunctionality.userOperations.PayOnline;
import org.poo.app.appFunctionality.userOperations.RejectSplitPayment;
import org.poo.app.appFunctionality.userOperations.Report;
import org.poo.app.appFunctionality.userOperations.SendMoney;
import org.poo.app.appFunctionality.userOperations.SetAlias;
import org.poo.app.appFunctionality.userOperations.SetMinimumBalance;
import org.poo.app.appFunctionality.userOperations.SpendingsReport;
import org.poo.app.appFunctionality.userOperations.SplitPayment;
import org.poo.app.appFunctionality.userOperations.UpgradePlan;
import org.poo.app.appFunctionality.userOperations.WithdrawSavings;


@Getter
public class CommandExecutor {
    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void addAccount(final CommandHandler handler, final ArrayNode output) {
        new AddAccount(handler, output).execute();
    }

    /**
     * Adds funds to the account
     * @param handler the command handler
     * @param output the output
     */
    public void addFunds(final CommandHandler handler, final ArrayNode output) {
        new AddFunds(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void cashWithdrawal(final CommandHandler handler, final ArrayNode output) {
        new CashWithdrawal(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void withdrawSavings(final CommandHandler handler, final ArrayNode output) {
        new WithdrawSavings(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void addInterest(final CommandHandler handler, final ArrayNode output) {
        new AddInterest(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void changeInterestRate(final CommandHandler handler, final ArrayNode output) {
        new ChangeInterestRate(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void checkCardStatus(final CommandHandler handler, final ArrayNode output) {
        new CheckCardStatus(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void createCard(final CommandHandler handler, final ArrayNode output) {
        new CreateCard(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void createOneTimeCard(final CommandHandler handler, final ArrayNode output) {
        new CreateOneTimeCard(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void deleteCard(final CommandHandler handler, final ArrayNode output) {
        new DeleteCard(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void deleteAccount(final CommandHandler handler, final ArrayNode output) {
        new DeleteAccount(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void payOnline(final CommandHandler handler, final ArrayNode output) {
        new PayOnline(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void report(final CommandHandler handler, final ArrayNode output) {
        new Report(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void sendMoney(final CommandHandler handler, final ArrayNode output) {
        new SendMoney(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void setAlias(final CommandHandler handler, final ArrayNode output) {
        new SetAlias(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void setMinimumBalance(final CommandHandler handler, final ArrayNode output) {
        new SetMinimumBalance(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void splitPayment(final CommandHandler handler, final ArrayNode output) {
        new SplitPayment(handler, output).sendRequestNotifications();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void spendingsReport(final CommandHandler handler, final ArrayNode output) {
        new SpendingsReport(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void printUsers(final CommandHandler handler, final ArrayNode output) {
        new PrintUsers(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void printTransactions(final CommandHandler handler, final ArrayNode output) {
        new PrintTransactions(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void upgradePlan(final CommandHandler handler, final ArrayNode output) {
        new UpgradePlan(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void acceptSplitPayment(final CommandHandler handler, final ArrayNode output) {
        new AcceptSplitPayment(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void rejectSplitPayment(final CommandHandler handler, final ArrayNode output) {
        new RejectSplitPayment(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void addNewBusinessAssociate(final CommandHandler handler, final ArrayNode output) {
        new AddNewBusinessAssociate(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void changeDepositLimit(final CommandHandler handler, final ArrayNode output) {
        new ChangeDepositLimit(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void changeSpendingLimit(final CommandHandler handler, final ArrayNode output) {
        new ChangeSpendingLimit(handler, output).execute();
    }

    /**
     * Executes the command
     * @param handler the command handler
     * @param output the output
     */
    public void businessReport(final CommandHandler handler, final ArrayNode output) {
        new BusinessReport(handler, output).execute();
    }
}
