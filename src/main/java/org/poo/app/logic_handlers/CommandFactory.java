package org.poo.app.logic_handlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.app_functionality.commands.*;
import org.poo.utils.Command;

class CommandFactory {
    private static final CommandExecutor executor = new CommandExecutor();

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

    public static Command getCommand(final CommandHandler handler, final ArrayNode output) {
        CommandType commandType = CommandType.valueOf(handler.getCommand());
        return switch (commandType) {
            case addAccount ->              new AddAccountCommand(executor, handler, output);
            case addFunds ->                new AddFundsCommand(executor, handler, output);
            case createCard ->              new CreateCardCommand(executor, handler, output);
            case createOneTimeCard ->       new CreateOneTimeCardCommand(executor, handler, output);
            case deleteCard ->              new DeleteCardCommand(executor, handler, output);
            case setMinimumBalance ->       new SetMinimumBalanceCommand(executor, handler, output);
            case sendMoney ->               new SendMoneyCommand(executor, handler, output);
            case setAlias ->                new SetAliasCommand(executor, handler, output);
            case splitPayment ->            new SplitPaymentCommand(executor, handler, output);
            case deleteAccount ->           new DeleteAccountCommand(executor, handler, output);
            case payOnline ->               new PayOnlineCommand(executor, handler, output);
            case checkCardStatus ->         new CheckCardStatusCommand(executor, handler, output);
            case report ->                  new ReportCommand(executor, handler, output);
            case addInterest ->             new AddInterestCommand(executor, handler, output);
            case changeInterestRate ->      new ChangeInterestRateCommand(executor, handler, output);
            case spendingsReport ->         new SpendingsReportCommand(executor, handler, output);
            case printUsers ->              new PrintUsersCommand(executor, handler, output);
            case printTransactions ->       new PrintTransactionsCommand(executor, handler, output);
            case upgradePlan ->             new UpgradePlanCommand(executor, handler, output);
            case withdrawSavings ->         new WithdrawSavingsCommand(executor, handler, output);
            case cashWithdrawal ->          new CashWithdrawalCommand(executor, handler, output);
            case acceptSplitPayment -> null;
            case rejectSplitPayment -> null;
            case addNewBusinessAssociate -> null;
            case changeSpendingLimit -> null;
            case businessReport -> null;
            case changeDepositLimit -> null;
        };
    }
}
