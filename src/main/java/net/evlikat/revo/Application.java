package net.evlikat.revo;

import com.google.gson.Gson;
import net.evlikat.revo.domain.Account;
import net.evlikat.revo.services.AccountService;
import net.evlikat.revo.services.BusinessException;
import net.evlikat.revo.services.InMemoryAccountService;
import net.evlikat.revo.web.AccountInfo;
import net.evlikat.revo.web.Message;
import net.evlikat.revo.web.TransferTask;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.internalServerError;
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

    private static final Gson GSON = new Gson();

    private static final String CONTENT_TYPE = "application/json";

    private final AccountService accountService;

    public Application() {
        // mmm, such DI
        this.accountService = new InMemoryAccountService();
    }

    public static void main(String[] args) {
        Application application = new Application();

        port(8080);

        get("/account/:accountId",
            (req, res) -> {
                res.type(CONTENT_TYPE);
                long accountId = Long.parseLong(req.params(":accountId"));
                Account newAccount = application.accountService.get(accountId);
                AccountInfo result = AccountInfo.from(newAccount);
                return result == null ? null : GSON.toJson(result);
            });

        get("/account",
            (req, res) -> {
                res.type(CONTENT_TYPE);
                List<AccountInfo> result = application.accountService.all().stream()
                    .map(AccountInfo::from)
                    .collect(Collectors.toList());
                return result == null ? null : GSON.toJson(result);
            });

        post("/account/:name",
            (req, res) -> {
                String name = req.params(":name");
                Account newAccount = application.accountService.createNew(name);
                AccountInfo result = AccountInfo.from(newAccount);
                res.type(CONTENT_TYPE);
                return result == null ? null : GSON.toJson(result);
            });

        post("/deposit",
            (Request req, Response res) -> {
                res.type(CONTENT_TYPE);
                TransferTask transferTask = GSON.fromJson(req.body(), TransferTask.class);
                application.accountService.deposit(
                    transferTask.getDestinationId(),
                    transferTask.getAmount());
                return GSON.toJson(new Message("operation complete"));
            });

        post("/withdraw",
            (Request req, Response res) -> {
                res.type(CONTENT_TYPE);
                TransferTask transferTask = GSON.fromJson(req.body(), TransferTask.class);
                application.accountService.withdraw(
                    transferTask.getSourceId(),
                    transferTask.getAmount());
                return GSON.toJson(new Message("operation complete"));
            });

        post("/transfer",
            (Request req, Response res) -> {
                res.type(CONTENT_TYPE);
                TransferTask transferTask = GSON.fromJson(req.body(), TransferTask.class);
                application.accountService.transfer(
                    transferTask.getSourceId(),
                    transferTask.getDestinationId(),
                    transferTask.getAmount());
                return GSON.toJson(new Message("operation complete"));
            });

        exception(IllegalArgumentException.class, (ex, req, res) -> {
            res.type(CONTENT_TYPE);
            res.body(GSON.toJson(new Message(ex.getMessage())));
            res.status(400);
        });

        exception(BusinessException.class, (ex, req, res) -> {
            res.type(CONTENT_TYPE);
            res.body(GSON.toJson(new Message(ex.getMessage())));
            res.status(400);
        });

        notFound((req, res) -> {
            res.type(CONTENT_TYPE);
            return GSON.toJson(new Message("not found"));
        });

        internalServerError((req, res) -> {
            res.type(CONTENT_TYPE);
            return GSON.toJson(new Message("internal error"));
        });
    }
}
