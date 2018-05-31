package net.evlikat.revo;

import com.google.gson.Gson;
import net.evlikat.revo.web.AccountInfo;
import net.evlikat.revo.web.Message;
import net.evlikat.revo.web.TransferTask;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *
 */
public class ApplicationTest {

    private static final Gson GSON = new Gson();

    private Application application = new Application();
    private HttpClient client = new HttpClient();

    private AccountInfo dad;
    private AccountInfo mom;

    @Before
    public void setUp() throws Exception {
        client.start();
        application = new Application();
        application.init();
        application.awaitInitialization();

        // Create two sample accounts
        dad = response(client.POST("http://localhost:8080/account/dad"), AccountInfo.class, 200);
        assertThat(dad.getId()).isNotNull();
        assertThat(dad.getName()).isEqualTo("dad");
        assertThat(dad.getMoney()).isEqualTo(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_FLOOR));

        mom = response(client.POST("http://localhost:8080/account/mom"), AccountInfo.class, 200);
        assertThat(mom.getId()).isNotNull();
        assertThat(mom.getName()).isEqualTo("mom");
        assertThat(mom.getMoney()).isEqualTo(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_FLOOR));

        assertThat(mom.getId()).isNotEqualTo(dad.getId());
    }

    @After
    public void tearDown() throws Exception {
        application.stop();
        awaitServerStops();
    }

    /**
     *
     */
    @Test
    public void shouldDepositMoney() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldNotDepositMoneyWhenInvalidValue() throws Exception {
        response(client.POST("http://localhost:8080/withdraw")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), -5_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldNotWithdrawMoneyWhenNotEnough() throws Exception {
        response(client.POST("http://localhost:8080/withdraw")
                .content(new StringContentProvider(GSON.toJson(withdraw(mom.getId(), 5_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldNotWithdrawMoneyWhenInvalidValue() throws Exception {
        response(client.POST("http://localhost:8080/withdraw")
                .content(new StringContentProvider(GSON.toJson(withdraw(mom.getId(), -5_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldDepositAndWithdrawMoney() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        response(client.POST("http://localhost:8080/withdraw")
                .content(new StringContentProvider(GSON.toJson(withdraw(mom.getId(), 5_00)))),
            Message.class, 200);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(5).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldDepositAndTransferMoney() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        response(client.POST("http://localhost:8080/transfer")
                .content(new StringContentProvider(GSON.toJson(transfer(mom.getId(), dad.getId(), 7_00)))),
            Message.class, 200);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);
        AccountInfo dadInfo = response(
            client.GET("http://localhost:8080/account/" + dad.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(3).setScale(2, BigDecimal.ROUND_FLOOR));
        assertThat(dadInfo.getMoney()).isEqualTo(BigDecimal.valueOf(7).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldDepositAndNotTransferMoneyWhenInvalidValue() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        response(client.POST("http://localhost:8080/transfer")
                .content(new StringContentProvider(GSON.toJson(transfer(mom.getId(), dad.getId(), -7_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);
        AccountInfo dadInfo = response(
            client.GET("http://localhost:8080/account/" + dad.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_FLOOR));
        assertThat(dadInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldDepositAndNotTransferMoneyWhenSameAccounts() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        response(client.POST("http://localhost:8080/transfer")
                .content(new StringContentProvider(GSON.toJson(transfer(mom.getId(), mom.getId(), 7_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);
        AccountInfo dadInfo = response(
            client.GET("http://localhost:8080/account/" + dad.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_FLOOR));
        assertThat(dadInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldDepositAndNotTransferMoneyWhenNotExistingAccount() throws Exception {
        response(client.POST("http://localhost:8080/deposit")
                .content(new StringContentProvider(GSON.toJson(deposit(mom.getId(), 10_00)))),
            Message.class, 200);

        response(client.POST("http://localhost:8080/transfer")
                .content(new StringContentProvider(GSON.toJson(transfer(mom.getId(), 99999, 7_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    /**
     *
     */
    @Test
    public void shouldNotTransferMoneyFromNotExistingAccount() throws Exception {

        response(client.POST("http://localhost:8080/transfer")
                .content(new StringContentProvider(GSON.toJson(transfer(99999, mom.getId(), 7_00)))),
            Message.class, 400);

        AccountInfo momInfo = response(
            client.GET("http://localhost:8080/account/" + mom.getId()),
            AccountInfo.class, 200);

        assertThat(momInfo.getMoney()).isEqualTo(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_FLOOR));
    }

    private TransferTask deposit(long id, long amount) {
        TransferTask transferTask = new TransferTask();
        transferTask.setDestinationId(id);
        transferTask.setAmount(amount);
        return transferTask;
    }

    private TransferTask withdraw(long id, long amount) {
        TransferTask transferTask = new TransferTask();
        transferTask.setSourceId(id);
        transferTask.setAmount(amount);
        return transferTask;
    }

    private TransferTask transfer(long sourceId, long destinationId, long amount) {
        TransferTask transferTask = new TransferTask();
        transferTask.setSourceId(sourceId);
        transferTask.setDestinationId(destinationId);
        transferTask.setAmount(amount);
        return transferTask;
    }

    private static <T> T response(Request request, Class<T> target, int expectedStatus) throws Exception {
        return response(request.send(), target, expectedStatus);
    }

    private static <T> T response(ContentResponse response, Class<T> target, int expectedStatus) throws Exception {
        assertThat(response.getStatus()).isEqualTo(expectedStatus);
        return GSON.fromJson(response.getContentAsString(), target);
    }

    private void awaitServerStops() throws InterruptedException {
        while (application.isRunning()) {
            Thread.sleep(10);
        }
    }
}
