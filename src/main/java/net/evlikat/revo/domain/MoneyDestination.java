package net.evlikat.revo.domain;

/**
 * MoneyDestination.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public interface MoneyDestination {

    void accept(Money money);
}
