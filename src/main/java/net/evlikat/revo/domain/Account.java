package net.evlikat.revo.domain;

import java.util.Objects;

/**
 * Account.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class Account implements MoneyDestination {

    private final Long id;
    private final String name;
    private volatile Money money;

    public Account(Long id, String name, Money money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getMoney() {
        return money;
    }

    public boolean has(Money amount) {
        return money.compareTo(amount) >= 0;
    }

    @Override
    public void accept(Money money) {
        this.money = this.money.add(money);
    }

    public void drainTo(Money money, MoneyDestination newDestination) {
        this.money = this.money.withdraw(money);
        newDestination.accept(money);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
            Objects.equals(name, account.name) &&
            Objects.equals(money, account.money);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, money);
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", money=" + money +
            '}';
    }
}
