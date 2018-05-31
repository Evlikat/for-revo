package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 * AccountService.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public final class AccountService {

    private final AtomicLong seq = new AtomicLong(1);
    private final ConcurrentHashMap<Long, LockableAccount> accounts = new ConcurrentHashMap<>();

    public AccountService() {
        Account bank = createNew("bank");
        bank.accept(Money.money(1_000_000_00));
        // IDs reserved for TOP clients
        seq.addAndGet(99);
    }

    /**
     *
     */
    public Account createNew(String name) {
        Long id = seq.getAndIncrement();
        Account account = new Account(id, name, Money.zero());
        accounts.put(id, new LockableAccount(account));
        return account;
    }

    /**
     *
     */
    public void transfer(long source, long destination, long amount) {
        // TODO: error handling
        if (amount == 0) {
            return;
        }
        Money moneyToMove = Money.money(amount);
        if (source == destination) {
            return;
        }
        LockableAccount srcAccount = accounts.get(source);
        if (srcAccount == null) {
            return;
        }
        LockableAccount dstAccount = accounts.get(destination);
        if (dstAccount == null) {
            return;
        }
        Lock lock1;
        Lock lock2;
        if (source < destination) {
            lock1 = srcAccount.lock();
            lock2 = dstAccount.lock();
        } else {
            lock1 = dstAccount.lock();
            lock2 = srcAccount.lock();
        }
        lock1.lock();
        lock2.lock();
        try {
            if (!srcAccount.has(amount)) {
                return;
            }
            srcAccount.drainTo(moneyToMove, dstAccount);
        } finally {
            lock1.unlock();
            lock2.unlock();
        }
    }

    /**
     *
     */
    public Account get(Long id) {
        LockableAccount lockableAccount = accounts.get(id);
        if (lockableAccount == null) {
            return null;
        }
        return lockableAccount.account();
    }
}
