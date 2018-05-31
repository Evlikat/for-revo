package net.evlikat.revo;

import com.google.gson.Gson;
import net.evlikat.revo.domain.Account;
import net.evlikat.revo.domain.Money;
import net.evlikat.revo.services.AccountService;
import net.evlikat.revo.web.AccountInfo;
import net.evlikat.revo.web.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * Main.
 *
 * @author Roman Prokhorov
 * @version 1.0
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static final Gson GSON = new Gson();

    private static final String CONTENT_TYPE = "application/json";

    private final AccountService accountService;

    public Application() {
        this.accountService = new AccountService();
    }

    public static void main(String[] args) {
        Application application = new Application();

        //TODO: remove
        initApp(application);

        port(8080);

        get("/account/:accountId",
            (req, res) -> {
                res.type(CONTENT_TYPE);
                long accountId = Long.parseLong(req.params(":accountId"));
                Account newAccount = application.accountService.get(accountId);
                AccountInfo result = AccountInfo.from(newAccount);
                return result == null ? null : GSON.toJson(result);
            });

        post("/transfer/:srcAccountId/to/:dstAccountId?amount=:amount",
            (req, res) -> {
                res.type(CONTENT_TYPE);
                long srcAccountId = Long.parseLong(req.params(":srcAccountId"));
                long dstAccountId = Long.parseLong(req.params(":dstAccountId"));
                long amount = Long.parseLong(req.params(":amount"));
                application.accountService.transfer(srcAccountId, dstAccountId, amount);
                return GSON.toJson(new Message("operation complete"));
            });

        notFound((req, res) -> {
            res.type(CONTENT_TYPE);
            return GSON.toJson(new Message("not found"));
        });
    }

    private static void initApp(Application application) {
        Account mom = application.accountService.createNew("Mom");
        Account dad = application.accountService.createNew("Dad");
        Account bank = application.accountService.get(1L);
        bank.drainTo(Money.money(15L), mom);
        bank.drainTo(Money.money(35L), dad);
    }
}
