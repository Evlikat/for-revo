package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static net.evlikat.revo.domain.Money.zero;

/**
 * AccountService.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public final class InMemoryAccountService implements AccountService {

    private final AtomicLong seq = new AtomicLong(1);
    private final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();

    /**
     *
     */
    @Override
    public Account createNew(String name) {
        Long id = seq.getAndIncrement();
        Account account = new Account(id, name, zero());
        accounts.put(id, account);
        return account;
    }

    /**
     *
     */
    @Override
    public List<Account> all() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public void transfer(Account srcAccount, Account dstAccount, Money moneyToMove) {
        if (zero().equals(moneyToMove)) {
            throw new BusinessException("at least one cents must be transferred");
        }
        if (Objects.equals(srcAccount.getId(), dstAccount.getId())) {
            throw new BusinessException("source and destination accounts must be different");
        }
        Lock lock1 = locks.computeIfAbsent(srcAccount.getId(), id -> new ReentrantLock(true));
        Lock lock2 = locks.computeIfAbsent(dstAccount.getId(), id -> new ReentrantLock(true));
        if (srcAccount.getId() > dstAccount.getId()) {
            Lock tmp = lock1;
            lock1 = lock2;
            lock2 = tmp;
        }
        lock1.lock();
        lock2.lock();
        try {
            if (!srcAccount.has(moneyToMove)) {
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
    public void transfer(long source, long destination, long amount) {
        Account srcAccount = accounts.get(source);
        if (srcAccount == null) {
            throw new BusinessException("source account not found");
        }
        Account dstAccount = accounts.get(destination);
        if (dstAccount == null) {
            throw new BusinessException("destination account not found");
        }
        transfer(srcAccount, dstAccount, Money.cents(amount));
    }

    /**
     *
     */
    @Override
    public Account get(Long id) {
        return accounts.get(id);
    }
}
