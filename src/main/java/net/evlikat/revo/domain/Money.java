package net.evlikat.revo.domain;

import java.math.BigDecimal;
import java.util.Objects;

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
    public static Money cents(long cents) {
        if (cents == 0) {
            return ZERO;
        }
        if (cents < 0) {
            throw new IllegalArgumentException("Negative values for money are not allowed");
        }
        return new Money(cents);
    }

    /**
     *
     */
    public static Money dollars(float dollars) {
        return cents(Math.round(dollars * 100.0));
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
        return cents(this.cents + money.cents);
    }

    public Money subtract(Money money) {
        return cents(this.cents - money.cents);
    }

    @Override
    public String toString() {
        return get().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money money = (Money) o;
        return cents == money.cents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cents);
    }
}
