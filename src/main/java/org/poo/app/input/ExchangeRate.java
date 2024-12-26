package org.poo.app.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.fileio.ExchangeInput;

@Data
@NoArgsConstructor
public class ExchangeRate {
    private String from;
    private String to;
    private double rate;
    private int timestamp;

    public ExchangeRate(final ExchangeInput exchange) {
        this.from = exchange.getFrom();
        this.to = exchange.getTo();
        this.rate = exchange.getRate();
        this.timestamp = exchange.getTimestamp();
    }

    public ExchangeRate(final String from, final String to,
                        final double rate, final int timestamp) {
        this.from = from;
        this.to = to;
        this.rate = rate;
        this.timestamp = timestamp;
    }
}
