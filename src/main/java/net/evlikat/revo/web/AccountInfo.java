package net.evlikat.revo.web;

import net.evlikat.revo.domain.Account;

import java.math.BigDecimal;

/**
 * Account.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class AccountInfo {

    private final Long id;
    private final String name;
    private final BigDecimal money;

    private AccountInfo(Long id, String name, BigDecimal money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    /**
     *
     */
    public static AccountInfo from(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountInfo(
            account.getId(),
            account.getName(),
            account.getMoney().get());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMoney() {
        return money;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", money=" + money +
            '}';
    }
}
