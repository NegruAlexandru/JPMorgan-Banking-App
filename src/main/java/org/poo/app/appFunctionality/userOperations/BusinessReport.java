package org.poo.app.appFunctionality.userOperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.baseClasses.Transaction;
import org.poo.app.baseClasses.User;
import org.poo.app.logicHandlers.CommandHandler;
import org.poo.app.logicHandlers.DB;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.BusinessAccount;
import org.poo.utils.Operation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeMap;

import static org.poo.app.logicHandlers.CommandHandler.OBJECT_MAPPER;

public class BusinessReport extends Operation {
    public BusinessReport(final CommandHandler handler,
                          final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Generate a report for a business account
     */
    @Override
    public void execute() {
        Account account = DB.findAccountByIBAN(handler.getAccount());

        if (account == null) {
            return;
        }

        if (!account.getType().equals("business")) {
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;
        createReport(businessAccount);
    }

    /**
     * Create a report for a business account
     * @param account the business account
     */
    private void createReport(final BusinessAccount account) {
        if (handler.getType().equals("transaction")) {
            createTransactionReport(account);
        } else if (handler.getType().equals("commerciant")) {
            createCommerciantReport(account);
        }
    }

    /**
     * Create a transaction report for a business account
     * @param account the business account
     */
    private void createTransactionReport(final BusinessAccount account) {
        ObjectNode report = output.addObject();
        report.put("command", handler.getCommand());

        ObjectNode data = OBJECT_MAPPER.createObjectNode();
        data.put("IBAN",
                account.getIban());
        data.put("balance",
                account.getBalance());
        data.put("currency",
                account.getCurrency());
        data.put("spending limit",
                account.getSpendingLimit());
        data.put("deposit limit",
                account.getDepositLimit());
        data.put("statistics type",
                "transaction");

        LinkedHashMap<User, Double> spending = new LinkedHashMap<>();
        LinkedHashMap<User, Double> deposited = new LinkedHashMap<>();
        double totalSpent = 0;
        double totalDeposited = 0;

        for (Transaction transaction : account.getBusinessTransactions()) {
            User user = transaction.getBusinessAssociatedUser();

            if (user == account.getOwner()) {
                continue;
            }

            if (transaction.getCommand().equals("sendMoney")
                || transaction.getCommand().equals("payOnline")) {
                if (spending.containsKey(user)) {
                    Double spent = spending.get(user);
                    spent += transaction.getAmountDouble();
                    spending.put(user, spent);
                } else {
                    spending.put(user, transaction.getAmountDouble());
                }
                totalSpent += transaction.getAmountDouble();
            } else if (transaction.getCommand().equals("addFunds")) {
                if (deposited.containsKey(user)) {
                    Double deposit = deposited.get(user);
                    deposit += transaction.getAmountDouble();
                    deposited.put(user, deposit);
                } else {
                    deposited.put(user, transaction.getAmountDouble());
                }
                totalDeposited += transaction.getAmountDouble();
            }
        }

        ArrayNode managers = OBJECT_MAPPER.createArrayNode();
        for (User manager : account.getManagers()) {
            double spentSum = 0.0;
            double depositedSum = 0.0;

            if (spending.containsKey(manager)) {
                spentSum = spending.get(manager);
            }

            if (deposited.containsKey(manager)) {
                depositedSum = deposited.get(manager);
            }

            ObjectNode managerNode = OBJECT_MAPPER.createObjectNode();
            managerNode.put("username",
                    manager.getLastName() + " " + manager.getFirstName());
            managerNode.put("spent",
                    spentSum);
            managerNode.put("deposited",
                    depositedSum);
            managers.add(managerNode);
        }

        ArrayNode employees = OBJECT_MAPPER.createArrayNode();
        for (User employee : account.getEmployees()) {
            double spentSum = 0.0;
            double depositedSum = 0.0;

            if (spending.containsKey(employee)) {
                spentSum = spending.get(employee);
            }

            if (deposited.containsKey(employee)) {
                depositedSum = deposited.get(employee);
            }

            ObjectNode employeeNode = OBJECT_MAPPER.createObjectNode();
            employeeNode.put("username",
                    employee.getLastName() + " " + employee.getFirstName());
            employeeNode.put("spent",
                    spentSum);
            employeeNode.put("deposited",
                    depositedSum);
            employees.add(employeeNode);
        }

        data.set("managers",
                managers);
        data.set("employees",
                employees);
        data.put("total spent",
                totalSpent);
        data.put("total deposited",
                totalDeposited);

        report.set("output",
                data);
        report.put("timestamp",
                handler.getTimestamp());
    }

    /**
     * Create a commerciant report for a business account
     * @param account the business account
     */
    private void createCommerciantReport(final BusinessAccount account) {
        ObjectNode report = output.addObject();
        report.put("command", handler.getCommand());
        report.put("timestamp", handler.getTimestamp());

        ObjectNode data = OBJECT_MAPPER.createObjectNode();
        data.put("IBAN", account.getIban());
        data.put("balance", account.getBalance());
        data.put("currency", account.getCurrency());
        data.put("spending limit", account.getSpendingLimit());
        data.put("deposit limit", account.getDepositLimit());

        TreeMap<String, HashMap<User, Double>> commerciantSpending = new TreeMap<>();
        HashMap<User, Double> userMapping = new HashMap<>();

        for (Transaction t : account.getBusinessTransactions()) {
            if (t.getCommerciant() == null) {
                continue;
            }

            if (t.getBusinessAssociatedUser() == account.getOwner()) {
                continue;
            }

            if (commerciantSpending.containsKey(t.getCommerciant())) {
                HashMap<User, Double> employees = commerciantSpending.get(t.getCommerciant());
                User user = t.getBusinessAssociatedUser();
                if (employees.containsKey(user)) {
                    double spent = employees.get(user);
                    spent += t.getAmountDouble();
                    employees.put(user, spent);
                    userMapping.put(user, spent);
                } else {
                    employees.put(user, t.getAmountDouble());
                }

            } else {
                User user = t.getBusinessAssociatedUser();
                commerciantSpending.put(t.getCommerciant(), new HashMap<>() {{
                    put(user, t.getAmountDouble());
                }});
            }
        }

        ArrayNode commerciants = OBJECT_MAPPER.createArrayNode();
        for (String keyCommerciant : commerciantSpending.keySet()) {
            HashMap<User, Double> employees = commerciantSpending.get(keyCommerciant);

            ObjectNode commerciant = OBJECT_MAPPER.createObjectNode();
            ArrayNode employeesArray = OBJECT_MAPPER.createArrayNode();
            ArrayNode managersArray = OBJECT_MAPPER.createArrayNode();
            commerciant.put("commerciant", keyCommerciant);

            double totalReceived = 0;

            for (User keyUser : employees.keySet()) {
                String name = keyUser.getLastName() + " " + keyUser.getFirstName();
                if (account.getEmployees().contains(keyUser)) {
                    employeesArray.add(name);
                    if (userMapping.containsKey(keyUser)
                        && Objects.equals(userMapping.get(keyUser), employees.get(keyUser))) {
                        employeesArray.add(name);
                    }
                } else {
                    managersArray.add(name);
                    if (userMapping.containsKey(keyUser)
                        && Objects.equals(userMapping.get(keyUser), employees.get(keyUser))) {
                        employeesArray.add(name);
                    }
                }

                totalReceived += employees.get(keyUser);
            }
            commerciant.put("total received", totalReceived);
            commerciant.set("employees", employeesArray);
            commerciant.set("managers", managersArray);

            commerciants.add(commerciant);
        }

        data.set("commerciants", commerciants);
        data.put("statistics type", "commerciant");
        report.set("output", data);
    }
}
