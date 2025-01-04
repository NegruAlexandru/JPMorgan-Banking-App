package org.poo.app.app_functionality.user_operations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.app.app_functionality.debug.Transaction;
import org.poo.app.input.User;
import org.poo.app.logic_handlers.CommandHandler;
import org.poo.app.logic_handlers.DB;
import org.poo.app.user_facilities.Account;
import org.poo.app.user_facilities.BusinessAccount;
import org.poo.utils.Operation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.poo.app.logic_handlers.CommandHandler.OBJECT_MAPPER;

public class BusinessReport extends Operation {
    public BusinessReport(final CommandHandler handler, final ArrayNode output) {
        super(handler, output);
    }

    /**
     * Generate a report for a business account
     */
    @Override
    public void execute() {
        Account account =DB.findAccountByIBAN(handler.getAccount());
        if (account == null) {
            // Account not found
            return;
        }

        if (!account.getType().equals("business")) {
            // Account is not of type business
            return;
        }

        BusinessAccount businessAccount = (BusinessAccount) account;
        // check ownership
        if (!businessAccount.getOwner().getEmail().equals(handler.getEmail())) {
            return;
        }

        createReport(businessAccount);
    }

    public void createReport(final BusinessAccount account) {
        if (handler.getType().equals("transaction")) {
            createTransactionReport(account);
        } else if (handler.getType().equals("commerciant")) {
            createCommerciantReport(account);
        }
    }

    public void createTransactionReport(final BusinessAccount account) {
        ObjectNode report = output.addObject();
        report.put("command", handler.getCommand());
        report.put("timestamp", handler.getTimestamp());

        ObjectNode data = OBJECT_MAPPER.createObjectNode();
        data.put("IBAN", account.getIban());
        data.put("balance", account.getBalance());
        data.put("currency", account.getCurrency());
        data.put("spending limit", account.getSpendingLimit());
        data.put("deposit limit", account.getDepositLimit());

        ArrayNode managers = OBJECT_MAPPER.createArrayNode();
        for (User manager : account.getManagers()) {
            ObjectNode managerNode = OBJECT_MAPPER.createObjectNode();
            managerNode.put("name",  manager.getLastName() + manager.getFirstName());
//            managerNode.put("spent", manager.getSpent());
//            managerNode.put("deposited", manager.getDeposited());
            managers.add(managerNode);
        }

        ArrayNode employees = OBJECT_MAPPER.createArrayNode();
        for (User employee : account.getEmployees()) {
            ObjectNode employeeNode = OBJECT_MAPPER.createObjectNode();
            employeeNode.put("name",  employee.getLastName() + employee.getFirstName());
//            employeeNode.put("spent", employee.getSpent());
//            employeeNode.put("deposited", employee.getDeposited());
            employees.add(employeeNode);
        }

        data.set("managers", managers);
        data.set("employees", employees);
        // data.set("total spent", account.getTotalSpent());
        // data.set("total deposited", account.getTotalDeposited());

        report.set("output", data);
    }

    public void createCommerciantReport(final BusinessAccount account) {
        ObjectNode report = output.addObject();
        report.put("command", handler.getCommand());
        report.put("timestamp", handler.getTimestamp());

        ObjectNode data = OBJECT_MAPPER.createObjectNode();
        data.put("IBAN", account.getIban());
        data.put("balance", account.getBalance());
        data.put("currency", account.getCurrency());
        data.put("spending limit", account.getSpendingLimit());
        data.put("deposit limit", account.getDepositLimit());

        LinkedHashMap <String, HashMap<User, Double>> commerciantSpending = new LinkedHashMap<>();

        for (Transaction t : account.getBusinessTransactions()) {
            if (commerciantSpending.containsKey(t.getCommerciant())) {
                HashMap<User, Double> employees = commerciantSpending.get(t.getCommerciant());
                User user = t.getBusinessAssociatedUser();
                if (employees.containsKey(user)) {
                    Double spent = employees.get(user);
                    spent += t.getAmountDouble();
                    employees.put(user, spent);
                } else {
                    employees.put(user, t.getAmountDouble());
                }

            } else {
                User user = t.getBusinessAssociatedUser();
                commerciantSpending.put(t.getCommerciant(), new HashMap<>());
                commerciantSpending.get(t.getCommerciant()).put(user, t.getAmountDouble());
            }
        }

        ArrayNode commerciants = OBJECT_MAPPER.createArrayNode();
        for (String keyCommerciant : commerciantSpending.keySet()) {
            HashMap<User, Double> employees = commerciantSpending.get(keyCommerciant);

            ObjectNode commerciant = OBJECT_MAPPER.createObjectNode();
            ArrayNode employeesArray = OBJECT_MAPPER.createArrayNode();
            ArrayNode managersArray = OBJECT_MAPPER.createArrayNode();
            commerciant.put("commerciant", keyCommerciant);

            double totalSpent = 0;

            for (User keyUser : employees.keySet()) {
                String name = keyUser.getLastName() + keyUser.getFirstName();
                if (account.getEmployees().contains(keyUser)) {
                    employeesArray.add(name);
                } else {
                    managersArray.add(name);
                }
                totalSpent += employees.get(keyUser);
            }
            commerciant.put("totalSpent", totalSpent);
            commerciant.set("employees", employeesArray);
            commerciant.set("managers", managersArray);

            commerciants.add(commerciant);
        }
    }

    public void createReportHeader() {

    }
}
