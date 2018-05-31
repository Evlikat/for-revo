package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * AccountService.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public final class InMemoryAccountService implements AccountService {

    private final AtomicLong seq = new AtomicLong(1);
    private final ConcurrentHashMap<Long, LockableAccount> accounts = new ConcurrentHashMap<>();

    public InMemoryAccountService() {
        Account bank = createNew("bank");
        bank.accept(Money.money(1_000_000_00));
        // IDs reserved for TOP clients
        seq.addAndGet(99);
    }

    /**
     *
     */
    @Override
    public Account createNew(String name) {
        Long id = seq.getAndIncrement();
        Account account = new Account(id, name, Money.zero());
        accounts.put(id, new LockableAccount(account));
        return account;
    }

    /**
     *
     */
    @Override
    public List<Account> all() {
        return accounts.values().stream().map(LockableAccount::account).collect(Collectors.toList());
    }

    /**
     *
     */
    @Override
    public void transfer(long source, long destination, long amount) {
        if (amount == 0) {
            throw new BusinessException("at least one cents must be transferred");
        }
        Money moneyToMove = Money.money(amount);
        if (source == destination) {
            throw new BusinessException("source and destination accounts must be different");
        }
        LockableAccount srcAccount = accounts.get(source);
        if (srcAccount == null) {
            throw new BusinessException("source account not found");
        }
        LockableAccount dstAccount = accounts.get(destination);
        if (dstAccount == null) {
            throw new BusinessException("destination account not found");
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
                throw new BusinessException("source account does not have enough money");
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
    @Override
    public Account get(Long id) {
        LockableAccount lockableAccount = accounts.get(id);
        if (lockableAccount == null) {
            return null;
        }
        return lockableAccount.account();
    }
}
