package net.evlikat.revo.services;

import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;
import org.junit.Before;
import org.junit.Test;

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
        dad.accept(dollars(5));
        // when
        service.transfer(dad, mom, dollars(3));

        assertThat(dad.getMoney()).isEqualTo(dollars(2));
        assertThat(mom.getMoney()).isEqualTo(dollars(3));
    }

    /**
     *
     */
    @Test(expected = BusinessException.class)
    public void shouldNotTransferWhenNotEnough() throws Exception {
        // given
        Account mom = service.createNew("Mom");
        Account dad = service.createNew("Dad");
        dad.accept(dollars(5));
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
        dad.accept(dollars(5));
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
        dad.accept(dollars(5));
        // when
        service.transfer(dad, dad, dollars(3));
    }
}
