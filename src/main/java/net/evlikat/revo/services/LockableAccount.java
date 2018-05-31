package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;
import net.evlikat.revo.domain.MoneyDestination;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockableAccount.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class LockableAccount implements MoneyDestination {

    private final Account account;
    private final Lock lock = new ReentrantLock(true);

    public LockableAccount(Account account) {
        this.account = account;
    }

    public Account account() {
        return account;
    }

    public Lock lock() {
        return lock;
    }

    public Long getId() {
        return account.getId();
    }

    public String getName() {
        return account.getName();
    }

    public Money getMoney() {
        return account.getMoney();
    }

    @Override
    public void accept(Money money) {
        account.accept(money);
    }

    public void drainTo(Money money, MoneyDestination newDestination) {
        account.drainTo(money, newDestination);
    }

    public boolean has(long amount) {
        return account.has(amount);
    }
}
