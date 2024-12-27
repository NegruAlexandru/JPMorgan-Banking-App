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
            OperationFactory.getOperation(this, output).execute();
        }
    }

