package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.*;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;

    private final Gson gson = new Gson();
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;

        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("unable to start database", e);
        }

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.get("/game", this::listGames);
        javalin.put("/game", this::joinGame);

        javalin.exception(BadRequestException.class, (e, ctx) -> sendError(ctx, 400, e.getMessage()));
        javalin.exception(AlreadyTakenException.class, (e, ctx) -> sendError(ctx, 403, e.getMessage()));
        javalin.exception(Exception.class, this::handleException);
        javalin.exception(UnauthorizedException.class, (e, ctx) -> sendError(ctx, 401, e.getMessage()));

    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }

    private void register(Context ctx) throws Exception {
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request); // enters next steps, all work traced through here
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) throws Exception {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.status(200); // assuming no exception thrown
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(new LogoutRequest(authToken));
        ctx.status(200);
        ctx.result("{}");
    }

    private void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        CreateGameRequest bodyRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);
        String gameName = (bodyRequest == null) ? null : bodyRequest.gameName();

        CreateGameResult result = gameService.createGame(new CreateGameRequest(authToken, gameName));
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void listGames(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        JoinGameRequest requestBody = gson.fromJson(ctx.body(), JoinGameRequest.class);
        String playerColor = requestBody == null ? null : requestBody.playerColor();
        int gameID = requestBody == null ? 0 : requestBody.gameID();

        gameService.joinGame(new JoinGameRequest(authToken, playerColor, gameID));
        ctx.status(200);
        ctx.result("{}");

    }

    private void sendError(Context ctx, int status, String message) {
        ctx.status(status);
        ctx.result(gson.toJson(Map.of("message", message)));
    }

    private void handleException(Exception e, Context ctx) {
        ctx.status(500);
        ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
