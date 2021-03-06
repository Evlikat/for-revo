package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * BEWARE! It is a ConcurrentTransferTest!
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class ConcurrentAccountTest {

    private InMemoryAccountService service;

    @Before
    public void setUp() throws Exception {
        service = new InMemoryAccountService();
    }

    /**
     *
     */
    @Test
    public void shouldCreateAccounts() throws Exception {
        int totalAccounts = 10_000;
        Set<Long> ids = IntStream.range(0, totalAccounts)
            .parallel()
            .mapToObj(i -> "Name #" + i)
            .map(service::createNew)
            .map(Account::getId)
            .collect(toSet());
        assertThat(ids)
            .hasSize(totalAccounts)
            .containsExactlyElementsOf(LongStream.range(1, 1 + totalAccounts).boxed().collect(toSet()));
    }

    /**
     *
     */
    @Test
    public void shouldDeposit() throws Exception {
        int totalAccounts = 3;
        int maxOperations = 10_000;
        IntStream.range(0, totalAccounts)
            .mapToObj(i -> "Name #" + i)
            .forEach(service::createNew);

        Random rnd = new Random();

        IntStream.range(0, maxOperations)
            .parallel()
            .forEach(num -> service.deposit(rnd.nextInt(totalAccounts) + 1, 1));

        assertThat(service.all()).hasSize(totalAccounts);
        assertThat(service.all().stream()
            .map(Account::getMoney)
            .map(Money::get)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(maxOperations, 2));
    }

    /**
     *
     */
    @Test
    public void shouldWithdraw() throws Exception {
        int totalAccounts = 3;
        // to prevent "not enough money" exception
        int initMoney = 1000;
        int maxOperations = 10_000;
        IntStream.range(0, totalAccounts)
            .mapToObj(i -> "Name #" + i)
            .map(service::createNew)
            .forEach(account -> account.deposit(Money.dollars(initMoney)));

        Random rnd = new Random();

        IntStream.range(0, maxOperations)
            .parallel()
            .forEach(num -> service.withdraw(rnd.nextInt(totalAccounts) + 1, 1));

        assertThat(service.all()).hasSize(totalAccounts);
        assertThat(service.all().stream()
            .map(Account::getMoney)
            .map(Money::get)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(totalAccounts * initMoney * 100 - maxOperations, 2));
    }

    /**
     *
     */
    @Test
    public void shouldTransfer() throws Exception {
        int totalAccounts = 3;
        // to prevent "not enough money" exception
        int initMoney = 10_000;
        int maxOperations = 10_000;
        IntStream.range(0, totalAccounts)
            .mapToObj(i -> "Name #" + i)
            .map(service::createNew)
            .forEach(account -> account.deposit(Money.dollars(initMoney)));

        Random rnd = new Random();

        IntStream.range(0, maxOperations)
            .parallel()
            .forEach(num -> {
                int id1 = rnd.nextInt(totalAccounts) + 1;
                int id2;
                do {
                    id2 = rnd.nextInt(totalAccounts) + 1;
                } while (id2 == id1);
                service.transfer(id1, id2, 1);
            });
        assertThat(service.all()).hasSize(totalAccounts);
        assertThat(service.all().stream()
            .map(Account::getMoney)
            .map(Money::get)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualTo(BigDecimal.valueOf(totalAccounts * initMoney * 100, 2));
    }
}
