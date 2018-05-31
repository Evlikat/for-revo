package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static net.evlikat.revo.domain.Money.dollars;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class InMemoryAccountServiceTest {

    private InMemoryAccountService service;

    @Before
    public void setUp() throws Exception {
        service = new InMemoryAccountService();
    }

    /**
     *
     */
    @Test
    public void shouldCreateAccount() throws Exception {
        Account mom = service.createNew("Mom");
        Account account = service.get(mom.getId());
        assertThat(account).isNotNull();
        assertThat(account.getName()).isEqualTo("Mom");
        assertThat(account.getMoney()).isEqualTo(Money.zero());
    }

    /**
     *
     */
    @Test
    public void shouldTransferMoney() throws Exception {
        // given
        Account mom = service.createNew("Mom");
        Account dad = service.createNew("Dad");
        dad.deposit(dollars(5));
        // when
        service.transfer(dad, mom, dollars(3));

        assertThat(dad.getMoney()).isEqualTo(dollars(2));
        assertThat(mom.getMoney()).isEqualTo(dollars(3));
    }

    /**
     *
     */
    @Test
    public void shouldDeposit() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        // when
        service.deposit(dad.getId(), 10);
        assertThat(dad.getMoney().get()).isEqualTo(BigDecimal.valueOf(10, 2));
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotDepositWhenZero() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        // when
        service.deposit(dad.getId(), 0);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDepositWhenNegative() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        // when
        service.deposit(dad.getId(), -1);
    }

    /**
     *
     */
    @Test
    public void shouldWithdraw() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        dad.deposit(dollars(10));
        // when
        service.withdraw(dad.getId(), 10);
        assertThat(dad.getMoney()).isEqualTo(dollars(9.9f));
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotWithdrawWhenZero() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        // when
        service.withdraw(dad.getId(), 0);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotWithdrawWhenNegative() throws Exception {
        // given
        Account dad = service.createNew("Dad");
        // when
        service.withdraw(dad.getId(), -1);
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotTransferWhenNotEnough() throws Exception {
        // given
        Account mom = service.createNew("Mom");
        Account dad = service.createNew("Dad");
        dad.deposit(dollars(5));
        // when
        service.transfer(dad, mom, dollars(10));
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotTransferWhenZero() throws Exception {
        // given
        Account mom = service.createNew("Mom");
        Account dad = service.createNew("Dad");
        dad.deposit(dollars(5));
        // when
        service.transfer(dad, mom, dollars(0));
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotTransferBetweenSameAccounts() throws Exception {
        // given
        Account mom = service.createNew("Mom");
        Account dad = service.createNew("Dad");
        dad.deposit(dollars(5));
        // when
        service.transfer(dad, dad, dollars(3));
    }
}
