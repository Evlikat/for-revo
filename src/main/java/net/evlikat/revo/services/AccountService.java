package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;

import java.util.List;

/**
 * AccountService.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public interface AccountService {

    /**
     * Creates a new account with specified name
     *
     * @param name name
     * @return new account object
     */
    Account createNew(String name);

    /**
     * Deposits money from source account to destination account
     *
     * @param destinationId id of destination account
     * @param amount        amount in cents
     */
    void deposit(long destinationId, long amount);

    /**
     * Transfers money from source account to destination account
     *
     * @param destinationAccount destination account
     * @param moneyToTransfer    money to transfer
     */
    void deposit(Account destinationAccount, Money moneyToTransfer);

    /**
     * Withdraws money from source account
     *
     * @param sourceAccount   source account
     * @param moneyToTransfer money to transfer
     */
    void withdraw(Account sourceAccount, Money moneyToTransfer);

    /**
     * Withdraws money from source account
     *
     * @param sourceId id of source account
     * @param amount   amount in cents
     */
    void withdraw(long sourceId, long amount);

    /**
     * @return accounts
     */
    List<Account> all();

    /**
     * Transfers money from source account to destination account
     *
     * @param sourceAccount      source account
     * @param destinationAccount destination account
     * @param moneyToTransfer    money to transfer
     */
    void transfer(Account sourceAccount, Account destinationAccount, Money moneyToTransfer);

    /**
     * Transfers money from source account to destination account
     *
     * @param sourceId      id of source account
     * @param destinationId id of destination account
     * @param amount        amount in cents
     */
    void transfer(long sourceId, long destinationId, long amount);

    /**
     * Returns account object by specified id
     *
     * @param accountId account id
     * @return account or null if not found
     */
    Account get(Long accountId);
}
