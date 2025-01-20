package org.poo.app.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Discount {
    private final String category;
    private final double value;
    private boolean used = false;

    public Discount(final String category,
                    final double value) {
        this.category = category;
        this.value = value;
    }
}
