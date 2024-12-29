package org.poo.app.logic_handlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.app.app_functionality.debug.PrintTransactions;
import org.poo.app.app_functionality.debug.PrintUsers;
import org.poo.app.app_functionality.user_operations.*;
import org.poo.utils.Operation;

class OperationFactory {
    public enum OperationType {
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

    public static Operation getOperation(final CommandHandler handler, final ArrayNode output) {
        OperationType operationType = OperationType.valueOf(handler.getCommand());
        return switch (operationType) {
            case addAccount -> new AddAccount(handler, output);
            case addFunds -> new AddFunds(handler, output);
            case createCard -> new CreateCard(handler, output);
            case createOneTimeCard -> new CreateOneTimeCard(handler, output);
            case deleteCard -> new DeleteCard(handler, output);
            case setMinimumBalance -> new SetMinimumBalance(handler, output);
            case sendMoney -> new SendMoney(handler, output);
            case setAlias -> new SetAlias(handler, output);
            case splitPayment -> new SplitPayment(handler, output);
            case deleteAccount -> new DeleteAccount(handler, output);
            case payOnline -> new PayOnline(handler, output);
            case checkCardStatus -> new CheckCardStatus(handler, output);
            case report -> new Report(handler, output);
            case addInterest -> new AddInterest(handler, output);
            case changeInterestRate -> new ChangeInterestRate(handler, output);
            case spendingsReport -> new SpendingsReport(handler, output);
            case printUsers -> new PrintUsers(handler, output);
            case printTransactions -> new PrintTransactions(handler, output);
            case upgradePlan -> new UpgradePlan(handler, output);
            case withdrawSavings -> new WithdrawSavings(handler, output);
            case cashWithdrawal -> new CashWithdrawal(handler, output);
            case acceptSplitPayment -> null;
            case rejectSplitPayment -> null;
            case addNewBusinessAssociate -> null;
            case changeSpendingLimit -> null;
            case businessReport -> null;
            case changeDepositLimit -> null;
        };
    }
}
