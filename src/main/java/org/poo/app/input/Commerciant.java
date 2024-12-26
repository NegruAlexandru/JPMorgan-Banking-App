package org.poo.app.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.fileio.CommerciantInput;

@Data
@NoArgsConstructor
public class Commerciant {
    private String commerciant;
    private int id;
    private String account;
    private String type;
    private String cashbackStrategy;

    public Commerciant(final CommerciantInput commerciantInput) {
        this.commerciant = commerciantInput.getCommerciant();
        this.id = commerciantInput.getId();
        this.account = commerciantInput.getAccount();
        this.type = commerciantInput.getType();
        this.cashbackStrategy = commerciantInput.getCashbackStrategy();
    }
}
