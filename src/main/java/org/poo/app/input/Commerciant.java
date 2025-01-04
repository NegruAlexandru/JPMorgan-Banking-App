package org.poo.app.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.app.user_facilities.Account;
import org.poo.fileio.CommerciantInput;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;
    private LinkedHashMap<Account, Integer> nrOfTransactionsOfUsers = new LinkedHashMap<>();
    private LinkedHashMap<Account, Double> totalSpentByUsers = new LinkedHashMap<>();

    public Commerciant(final CommerciantInput commerciantInput) {
        this.commerciant = commerciantInput.getCommerciant();
        this.id = commerciantInput.getId();
        this.account = commerciantInput.getAccount();
        this.type = commerciantInput.getType();
        this.cashbackStrategy = commerciantInput.getCashbackStrategy();
    }
}
