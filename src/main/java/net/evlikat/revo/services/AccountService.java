package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;

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

    List<Account> all();

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
