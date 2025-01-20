package org.poo.app.logicHandlers;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.poo.app.baseClasses.Commerciant;
import org.poo.app.payment.ExchangeRate;
import org.poo.app.baseClasses.User;
import org.poo.app.userFacilities.Account;
import org.poo.app.userFacilities.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public abstract class DB {
    @Getter
    private static ArrayList<ExchangeRate> exchangeRates = new ArrayList<>();
    @Getter
    private static ArrayList<String> currencies = new ArrayList<>();
    @Getter
    private static ArrayList<Commerciant> commerciants = new ArrayList<>();
    @Getter
    private static LinkedHashMap<String, User> users = new LinkedHashMap<>();
    @Getter
    private static HashMap<String, Account> accountsWithIBAN = new HashMap<>();
    @Getter
    private static HashMap<String, Account> accountsWithCardNumber = new HashMap<>();
    @Getter
    private static ArrayList<String> commerciantAccounts = new ArrayList<>();

    /**
     * Add the derived exchange rate to the list if it is not already present
     * @param newRate the new exchange rate
     * @return true if the exchange rate is already present, false otherwise
     */
    public static boolean addDerivedExchangeRate(final ExchangeRate newRate) {
        boolean isPresent = false;
        for (ExchangeRate r : exchangeRates) {
            if (r.getFrom().equals(newRate.getFrom()) && r.getTo().equals(newRate.getTo())) {
                isPresent = true;
                break;
            }
        }

        if (!isPresent) {
            exchangeRates.add(newRate);
            ExchangeRate reversedNewRate = new ExchangeRate(newRate.getTo(),
                    newRate.getFrom(),
                    1 / newRate.getRate(),
                    newRate.getTimestamp());
            if (!exchangeRates.contains(reversedNewRate)) {
                exchangeRates.add(reversedNewRate);
            }
            return false;
        }

        return true;
    }

    /**
     * Add the exchange rate to the list if it is not already present
     * and all the derived exchange rates
     * @param exchangeRate the new exchange rate
     */
    public static void addExchangeRate(final ExchangeRate exchangeRate) {
        // add the exchange rate to the same currency
        if (!currencies.contains(exchangeRate.getFrom())) {
            currencies.add(exchangeRate.getFrom());
        }

        if (!currencies.contains(exchangeRate.getTo())) {
            currencies.add(exchangeRate.getTo());
        }

        // add the exchange rate to the list
        if (!exchangeRates.contains(exchangeRate)) {
            exchangeRates.add(exchangeRate);
        }

        // add the reversed exchange rate to the list
        ExchangeRate reversedExchangeRate = new ExchangeRate(exchangeRate.getTo(),
                exchangeRate.getFrom(),
                1 / exchangeRate.getRate(),
                exchangeRate.getTimestamp());
        if (!exchangeRates.contains(reversedExchangeRate)) {
            exchangeRates.add(reversedExchangeRate);
        }

        // add the derived exchange rates
        boolean isConverted = false;
        while (!isConverted) {
            isConverted = true;
            for (ExchangeRate rate : exchangeRates) {
                if (rate.getFrom().equals(exchangeRate.getTo())) {
                    ExchangeRate newRate = new ExchangeRate(exchangeRate.getFrom(),
                            rate.getTo(),
                            exchangeRate.getRate() * rate.getRate(),
                            exchangeRate.getTimestamp());
                    isConverted = addDerivedExchangeRate(newRate);

                    if (!isConverted) {
                        break;
                    }
                }
            }
        }

        isConverted = false;
        while (!isConverted) {
            isConverted = true;
            for (ExchangeRate rate : exchangeRates) {
                if (rate.getTo().equals(exchangeRate.getFrom())) {
                    ExchangeRate newRate = new ExchangeRate(rate.getFrom(),
                            exchangeRate.getTo(),
                            exchangeRate.getRate() * rate.getRate(),
                            exchangeRate.getTimestamp());
                    isConverted = addDerivedExchangeRate(newRate);

                    if (!isConverted) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Get the exchange rate from a currency to another
     * @param from the currency to convert from
     * @param to the currency to convert to
     * @return the exchange rate
     */
    public static ExchangeRate getExchangeRate(final String from,
                                               final String to) {
        for (ExchangeRate exchangeRate : exchangeRates) {
            if (exchangeRate.getFrom().equals(from) && exchangeRate.getTo().equals(to)) {
                return exchangeRate;
            }
        }
        return new ExchangeRate(from, to, 1, 0);
    }

    /**
     * Convert an amount from a currency to another
     * @param amount the amount to convert
     * @param from the currency to convert from
     * @param to the currency to convert to
     * @return the converted amount
     */
    public static double convert(final double amount,
                                 final String from,
                                 final String to) {
        ExchangeRate exchangeRate = getExchangeRate(from, to);
        return amount * exchangeRate.getRate();
    }

    /**
     * Add a commerciant to the database
     * @param commerciant the commerciant to add
     */
    public static void addCommerciant(final Commerciant commerciant) {
        commerciants.add(commerciant);
        commerciantAccounts.add(commerciant.getAccount());
    }

    /**
     * Add a user to the database
     * @param user the user to add
     */
    public static void addUser(final User user) {
        users.put(user.getEmail(), user);
    }

    /**
     * Add an account to the database
     * @param account the account to add
     */
    public static void addAccount(final Account account) {
        accountsWithIBAN.put(account.getIban(), account);
    }

    /**
     * Add an account with a card number as a key to the database
     * @param account the account to add
     * @param cardNumber the card number to add
     */
    public static void addAccountWithCardNumber(final Account account,
                                                final String cardNumber) {
        accountsWithCardNumber.put(cardNumber, account);
    }

    /**
     * Remove an account from the database
     * @param account the account to remove
     */
    public static void removeAccount(final Account account) {
        accountsWithIBAN.remove(account.getIban());
    }

    /**
     * Remove an account with a card number as a key from the database
     * @param cardNumber the card number to remove
     */
    public static void removeAccountWithCardNumber(final String cardNumber) {
        accountsWithCardNumber.remove(cardNumber);
    }

    /**
     * Find an account by IBAN
     * @param iban the IBAN to search for
     * @return the account with the given IBAN
     */
    public static Account findAccountByIBAN(final String iban) {
        return DB.accountsWithIBAN.get(iban);
    }

    /**
     * Find a user by email
     * @param email the email to search for
     * @return the user with the given email
     */
    public static User findUserByEmail(final String email) {
        return DB.users.get(email);
    }

    /**
     * Find an account by card number
     * @param cardNumber the card number to search for
     * @return the account with the given card number
     */
    public static Account findAccountByCardNumber(final String cardNumber) {
        return DB.accountsWithCardNumber.get(cardNumber);
    }

    /**
     * Get a card by card number
     * @param cardNumber the card number to search for
     * @return the card with the given card number
     */
    public static Card getCardByCardNumber(final String cardNumber) {
        Account account = accountsWithCardNumber.get(cardNumber);
        if (account != null) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    return card;
                }
            }
        }

        return null;
    }

    /**
     * Get a commerciant by name
     * @param name the name to search for
     * @return the commerciant with the given name
     */
    public static Commerciant getCommerciantByName(final String name) {
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getCommerciant().equals(name)) {
                return commerciant;
            }
        }

        return null;
    }

    /**
     * Get a commerciant by IBAN
     * @param iban the IBAN to search for
     * @return the commerciant with the given IBAN
     */
    public static Commerciant getCommerciantByIBAN(final String iban) {
        for (Commerciant commerciant : commerciants) {
            if (commerciant.getAccount().equals(iban)) {
                return commerciant;
            }
        }

        return null;
    }

    /**
     * Reset the database
     */
    public static void reset() {
        exchangeRates.clear();
        currencies.clear();
        commerciants.clear();
        users.clear();
        accountsWithIBAN.clear();
        accountsWithCardNumber.clear();
    }
}
