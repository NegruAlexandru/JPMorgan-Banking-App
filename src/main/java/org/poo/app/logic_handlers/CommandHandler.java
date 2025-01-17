package org.poo.app.logic_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.poo.app.user_facilities.Card;
import org.poo.fileio.CommandInput;
import org.poo.utils.Command;
import org.poo.utils.Operation;

import java.util.ArrayList;
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
    private String splitPaymentType;
    private List<Double> amountForUsers;
    private String role;
    private String type;
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String ibannenorocit = "RO33POOB4049920806186500";
    public String emailnenorocit = emailnenorocitget(ibannenorocit);
    public ArrayList<String> cardurinenorocite = cardurinenorociteget(ibannenorocit);

    public static String emailnenorocitget(final String in) {
        if (DB.findAccountByIBAN(in) != null) {
            return DB.findAccountByIBAN(in).getEmail();
        }

        return null;
    }

    public static ArrayList<String> cardurinenorociteget(final String in) {
        ArrayList<String> carduri = new ArrayList<>();
        if (DB.findAccountByIBAN(in) != null) {
            for (Card card : DB.findAccountByIBAN(in).getCards()) {
                carduri.add(card.getCardNumber());
            }
        }

        return carduri;
    }

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
        this.newPlanType = command.getNewPlanType();
        this.splitPaymentType = command.getSplitPaymentType();
        this.amountForUsers = command.getAmountForUsers();
        this.role = command.getRole();
        this.type = command.getType();
    }

        /**
         * Select the command to execute and create the response
         * @param output ArrayNode to add the response to
         */
        public void executeCommandAndCreateResponse(final ArrayNode output) {
//            CommandFactory.getCommand(this, output).execute();
            Command command = CommandFactory.getCommand(this, output);
            if (this.getAccount() != null && this.getAccount().equals(ibannenorocit)) {
                System.out.println();
                System.out.println("Command: " + this.getCommand());
                System.out.println("timestamp: " + this.getTimestamp());
                if (this.getAmount() != 0) {
                    System.out.println("Amount: " + this.getAmount());
                    System.out.println("Currency: " + this.getCurrency());
                    System.out.println("Card:" + this.getCardNumber());
                }
            } else if (this.getCardNumber() != null) {
                System.out.println();
                for (String card : cardurinenorocite) {
                    if (this.getCardNumber().equals(card)) {
                        System.out.println("Command: " + this.getCommand());
                        System.out.println("timestamp: " + this.getTimestamp());
                        if (this.getAmount() != 0) {
                            System.out.println("Amount: " + this.getAmount());
                            System.out.println("Currency: " + this.getCurrency());
                            System.out.println("Card:" + this.getCardNumber());

                        }
                    }
                }
            } else if (this.getEmail() != null && this.getEmail().equals(emailnenorocit)) {
                System.out.println();
                System.out.println("Command: " + this.getCommand());
                System.out.println("timestamp: " + this.getTimestamp());
                if (this.getAmount() != 0) {
                    System.out.println("Amount: " + this.getAmount());
                    System.out.println("Currency: " + this.getCurrency());
                    System.out.println("Card:" + this.getCardNumber());

                }
            }

            command.execute();
        }
    }

