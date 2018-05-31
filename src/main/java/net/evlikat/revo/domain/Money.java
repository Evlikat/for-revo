package net.evlikat.revo.domain;

import java.math.BigDecimal;

/**
 * Money.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class Money implements Comparable<Money> {

    private static final Money ZERO = new Money(0);

    private final long cents;

    private Money(long cents) {
        this.cents = cents;
    }

    /**
     *
     */
    public static Money money(long cents) {
        if (cents == 0) {
            return ZERO;
        }
        if (cents < 0) {
            throw new IllegalArgumentException("Negative values for money are not allowed");
        }
        return new Money(cents);
    }

    public BigDecimal get() {
        return BigDecimal.valueOf(cents, 2).setScale(2, BigDecimal.ROUND_FLOOR);
    }

    public static Money zero() {
        return ZERO;
    }

    @Override
    public int compareTo(Money o) {
        return Long.compare(this.cents, o.cents);
    }

    public Money add(Money money) {
        return money(this.cents + money.cents);
    }

    public Money withdraw(Money money) {
        return money(this.cents - money.cents);
    }

    @Override
    public String toString() {
        return get().toString();
    }
}
