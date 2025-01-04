package org.poo.app.user_facilities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Discount {
    private final String category;
    private final double value;

    public Discount(final String category, final double value) {
        this.category = category;
        this.value = value;
    }
}
