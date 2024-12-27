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
        cashWithdrawal
    }

    public static Operation getOperation(final CommandHandler handler, final ArrayNode output) {
        OperationType operationType = OperationType.valueOf(handler.getCommand());
        switch (operationType) {
            case addAccount:         return new AddAccount(handler, output);
            case addFunds:           return new AddFunds(handler, output);
            case createCard:         return new CreateCard(handler, output);
            case createOneTimeCard:  return new CreateOneTimeCard(handler, output);
            case deleteCard:         return new DeleteCard(handler, output);
            case setMinimumBalance:  return new SetMinimumBalance(handler, output);
            case sendMoney:          return new SendMoney(handler, output);
            case setAlias:           return new SetAlias(handler, output);
            case splitPayment:       return new SplitPayment(handler, output);
            case deleteAccount:      return new DeleteAccount(handler, output);
            case payOnline:          return new PayOnline(handler, output);
            case checkCardStatus:    return new CheckCardStatus(handler, output);
            case report:             return new Report(handler, output);
            case addInterest:        return new AddInterest(handler, output);
            case changeInterestRate: return new ChangeInterestRate(handler, output);
            case spendingsReport:    return new SpendingsReport(handler, output);
            case printUsers:         return new PrintUsers(handler, output);
            case printTransactions:  return new PrintTransactions(handler, output);
            case upgradePlan:        return new UpgradePlan(handler, output);
            case withdrawSavings:    return new WithdrawSavings(handler, output);
            case cashWithdrawal:     return new CashWithdrawal(handler, output);
        }
        throw new IllegalArgumentException("Invalid operation type");
    }
}
