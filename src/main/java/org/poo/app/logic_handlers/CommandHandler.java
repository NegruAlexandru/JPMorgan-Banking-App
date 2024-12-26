package org.poo.app.logic_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.app.app_functionality.debug.PrintUsers;
import org.poo.app.app_functionality.debug.PrintTransactions;

import org.poo.app.app_functionality.user_operations.*;

import org.poo.fileio.CommandInput;

import java.util.List;

@Data
@NoArgsConstructor
public class CommandHandler {
    private String command;
    private String email;
    private String account;
    private String currency;
    private double amount;
    private double minBalance;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private String receiver;
    private String alias;
    private String accountType;
    private double interestRate;
    private List<String> accounts;
    private String errorMessage;
    private String classicAccountIBAN;
    private String savingsAccountIBAN;
    private String newPlanType;
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final AccountHandler ACCOUNT_HANDLER = new AccountHandler();
    private List<String> debugCommands = List.of("printUsers", "printTransactions");
    private List<String> outputCommands = List.of("deleteAccount", "payOnline", "checkCardStatus",
            "report", "addInterest", "changeInterestRate", "spendingsReport");
    private List<String> commands = List.of("addAccount", "addFunds", "createCard",
            "createOneTimeCard", "deleteCard", "setMinimumBalance",
            "sendMoney", "setAlias", "splitPayment", "withdrawSavings", "cashWithdrawal",
            "upgradePlan");

    public CommandHandler(final CommandInput command) {
        this.command = command.getCommand();
        this.email = command.getEmail();
        this.account = command.getAccount();
        this.currency = command.getCurrency();
        this.amount = command.getAmount();
        this.minBalance = command.getMinBalance();
        this.target = command.getTarget();
        this.description = command.getDescription();
        this.cardNumber = command.getCardNumber();
        this.commerciant = command.getCommerciant();
        this.timestamp = command.getTimestamp();
        this.startTimestamp = command.getStartTimestamp();
        this.endTimestamp = command.getEndTimestamp();
        this.receiver = command.getReceiver();
        this.alias = command.getAlias();
        this.accountType = command.getAccountType();
        this.interestRate = command.getInterestRate();
        this.accounts = command.getAccounts();
    }

    /**
     * Select the command to execute and create the response
     * @param output ArrayNode to add the response to
     */
    public void executeCommandAndCreateResponse(final ArrayNode output) {
        if (debugCommands.contains(command)) {
            ArrayNode result = executeDebugCommands();
            ObjectNode node = OBJECT_MAPPER.createObjectNode();
            node.put("command", command);
            node.set("output", result);
            node.put("timestamp", getTimestamp());
            output.add(node);
        } else if (outputCommands.contains(command)) {
            ObjectNode result = executeCommandsWithOutput();
            if (result == null) {
                return;
            }
            ObjectNode node = OBJECT_MAPPER.createObjectNode();
            node.put("command", command);
            node.set("output", result);
            node.put("timestamp", getTimestamp());
            output.add(node);
        } else if (commands.contains(command)) {
            executeCommandsWithoutOutput();
        }
    }

    /**
     * Execute debug commands
     * @return ArrayNode with the output of the command
     */
    public ArrayNode executeDebugCommands() {
        return switch (command) {
            case "printUsers" -> PrintUsers.execute();
            case "printTransactions" ->
                    PrintTransactions.execute(this);
            default -> null;
        };
    }

    /**
     * Execute commands that return an output
     * @return ObjectNode with the output of the command
     */
    public ObjectNode executeCommandsWithOutput() {
        return switch (command) {
            case "deleteAccount" -> DeleteAccount.execute(this);
            case "payOnline" -> PayOnline.execute(this);
            case "checkCardStatus" -> CheckCardStatus.execute(this);
            case "report" -> Report.execute(this);
            case "addInterest" -> AddInterest.execute(this);
            case "changeInterestRate" -> ChangeInterestRate.execute(this);
            case "spendingsReport" -> SpendingsReport.execute(this);
            default -> null;
        };
    }

    /**
     * Execute commands that do not return an output
     */
    public void executeCommandsWithoutOutput() {
        switch (command) {
            case "addAccount" -> AddAccount.execute(this);
            case "addFunds" -> AddFunds.execute(this);
            case "createCard" -> CreateCard.execute(this);
            case "createOneTimeCard" -> CreateOneTimeCard.execute(this);
            case "deleteCard" -> DeleteCard.execute(this);
            case "setMinimumBalance" -> SetMinimumBalance.execute(this);
            case "sendMoney" -> SendMoney.execute(this);
            case "setAlias" -> SetAlias.execute(this);
            case "splitPayment" -> SplitPayment.execute(this);
            case "withdrawSavings" -> WithdrawSavings.execute(this);
            case "cashWithdrawal" -> CashWithdrawal.execute(this);
            case "upgradePlan" -> UpgradePlan.execute(this);
            default -> { }
        }
    }
}
