package org.poo.app.logicHandlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.appFunctionality.commands.AcceptSplitPaymentCommand;
import org.poo.app.appFunctionality.commands.AddAccountCommand;
import org.poo.app.appFunctionality.commands.AddFundsCommand;
import org.poo.app.appFunctionality.commands.AddInterestCommand;
import org.poo.app.appFunctionality.commands.AddNewBusinessAssociateCommand;
import org.poo.app.appFunctionality.commands.BusinessReportCommand;
import org.poo.app.appFunctionality.commands.CashWithdrawalCommand;
import org.poo.app.appFunctionality.commands.ChangeDepositLimitCommand;
import org.poo.app.appFunctionality.commands.ChangeInterestRateCommand;
import org.poo.app.appFunctionality.commands.ChangeSpendingLimitCommand;
import org.poo.app.appFunctionality.commands.CheckCardStatusCommand;
import org.poo.app.appFunctionality.commands.CreateCardCommand;
import org.poo.app.appFunctionality.commands.CreateOneTimeCardCommand;
import org.poo.app.appFunctionality.commands.DeleteAccountCommand;
import org.poo.app.appFunctionality.commands.DeleteCardCommand;
import org.poo.app.appFunctionality.commands.PayOnlineCommand;
import org.poo.app.appFunctionality.commands.PrintTransactionsCommand;
import org.poo.app.appFunctionality.commands.PrintUsersCommand;
import org.poo.app.appFunctionality.commands.ReportCommand;
import org.poo.app.appFunctionality.commands.RejectSplitPaymentCommand;
import org.poo.app.appFunctionality.commands.SendMoneyCommand;
import org.poo.app.appFunctionality.commands.SetAliasCommand;
import org.poo.app.appFunctionality.commands.SetMinimumBalanceCommand;
import org.poo.app.appFunctionality.commands.SpendingsReportCommand;
import org.poo.app.appFunctionality.commands.SplitPaymentCommand;
import org.poo.app.appFunctionality.commands.UpgradePlanCommand;
import org.poo.app.appFunctionality.commands.WithdrawSavingsCommand;
import org.poo.utils.Command;

class CommandFactory {
    private static final CommandExecutor EXECUTOR = new CommandExecutor();

    public enum CommandType {
        addAccount,
        addFunds,
        createCard,
        createOneTimeCard,
        deleteCard,
        setMinimumBalance,
        sendMoney,
        setAlias,
        splitPayment,
        deleteAccount,
        payOnline,
        checkCardStatus,
        report,
        addInterest,
        changeInterestRate,
        spendingsReport,
        printUsers,
        printTransactions,
        upgradePlan,
        withdrawSavings,
        cashWithdrawal,
        acceptSplitPayment,
        rejectSplitPayment,
        addNewBusinessAssociate,
        changeSpendingLimit,
        businessReport,
        changeDepositLimit
    }

    /**
     * Returns the command object corresponding to the command type
     * @param handler first argument of the command
     * @param output second argument of the command
     * @return Command object
     */
    public static Command getCommand(final CommandHandler handler, final ArrayNode output) {
        CommandType commandType = CommandType.valueOf(handler.getCommand());
        return switch (commandType) {
            case addAccount ->              new AddAccountCommand(
                    EXECUTOR,
                    handler,
                    output);
            case addFunds ->                new AddFundsCommand(
                    EXECUTOR,
                    handler,
                    output);
            case createCard ->              new CreateCardCommand(
                    EXECUTOR,
                    handler,
                    output);
            case createOneTimeCard ->       new CreateOneTimeCardCommand(
                    EXECUTOR,
                    handler,
                    output);
            case deleteCard ->              new DeleteCardCommand(
                    EXECUTOR,
                    handler,
                    output);
            case setMinimumBalance ->       new SetMinimumBalanceCommand(
                    EXECUTOR,
                    handler,
                    output);
            case sendMoney ->               new SendMoneyCommand(
                    EXECUTOR,
                    handler,
                    output);
            case setAlias ->                new SetAliasCommand(
                    EXECUTOR,
                    handler,
                    output);
            case splitPayment ->            new SplitPaymentCommand(
                    EXECUTOR,
                    handler,
                    output);
            case deleteAccount ->           new DeleteAccountCommand(
                    EXECUTOR,
                    handler,
                    output);
            case payOnline ->               new PayOnlineCommand(
                    EXECUTOR,
                    handler,
                    output);
            case checkCardStatus ->         new CheckCardStatusCommand(
                    EXECUTOR,
                    handler,
                    output);
            case report ->                  new ReportCommand(
                    EXECUTOR,
                    handler,
                    output);
            case addInterest ->             new AddInterestCommand(
                    EXECUTOR,
                    handler,
                    output);
            case changeInterestRate ->      new ChangeInterestRateCommand(
                    EXECUTOR,
                    handler,
                    output);
            case spendingsReport ->         new SpendingsReportCommand(
                    EXECUTOR,
                    handler,
                    output);
            case printUsers ->              new PrintUsersCommand(
                    EXECUTOR,
                    handler,
                    output);
            case printTransactions ->       new PrintTransactionsCommand(
                    EXECUTOR,
                    handler,
                    output);
            case upgradePlan ->             new UpgradePlanCommand(
                    EXECUTOR,
                    handler,
                    output);
            case withdrawSavings ->         new WithdrawSavingsCommand(
                    EXECUTOR,
                    handler,
                    output);
            case cashWithdrawal ->          new CashWithdrawalCommand(
                    EXECUTOR,
                    handler,
                    output);
            case acceptSplitPayment ->      new AcceptSplitPaymentCommand(
                    EXECUTOR,
                    handler,
                    output);
            case rejectSplitPayment ->      new RejectSplitPaymentCommand(
                    EXECUTOR,
                    handler,
                    output);
            case addNewBusinessAssociate -> new AddNewBusinessAssociateCommand(
                    EXECUTOR,
                    handler,
                    output);
            case changeSpendingLimit ->     new ChangeSpendingLimitCommand(
                    EXECUTOR,
                    handler,
                    output);
            case changeDepositLimit ->      new ChangeDepositLimitCommand(
                    EXECUTOR,
                    handler,
                    output);
            case businessReport ->          new BusinessReportCommand(
                    EXECUTOR,
                    handler,
                    output);
        };
    }
}
