package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameDAO gameDAO;
    private GameService gameService;
    private final String validToken = "Tokendorf";
    private final String validToken2 = "Tokarina-of-Time";

    @BeforeEach
    public void setUp() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData(validToken, "Link"));
        authDAO.createAuth(new AuthData(validToken2, "Zelda"));
    }

    @Test
    public void createGameSuccess() throws UnauthorizedException, BadRequestException, DataAccessException {
        CreateGameResult game = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));

        assertNotNull(gameDAO.getGame(game.gameID()));
    }

    @Test
    public void createGameInvalidToken() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(new CreateGameRequest("Groose-Token", "Four Swords")));
    }

    @Test
    public void createGameMissingName() {
        assertThrows(BadRequestException.class, () -> gameService.createGame(new CreateGameRequest(validToken, null)));
    }

    @Test
    public void listGamesSuccess() throws UnauthorizedException, DataAccessException, BadRequestException {
        gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));
        gameService.createGame(new CreateGameRequest(validToken, "Majora's Mask"));

        ListGamesResult result = gameService.listGames(new ListGamesRequest(validToken));

        assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesInvalidToken() throws UnauthorizedException, DataAccessException, BadRequestException {
        gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));
        gameService.createGame(new CreateGameRequest(validToken, "Majora's Mask"));

        assertThrows(UnauthorizedException.class, () -> gameService.listGames(new ListGamesRequest("LLort-token")));
    }

    @Test
    public void joinGameSuccess() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        CreateGameResult game = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));

        gameService.joinGame(new JoinGameRequest(validToken, "WHITE", game.gameID()));

        assertEquals("Link", gameDAO.getGame(game.gameID()).whiteUsername());
    }

    @Test
    public void joinGameAlreadyTaken() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        CreateGameResult game = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));
        gameService.joinGame(new JoinGameRequest(validToken, "WHITE", game.gameID()));

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(new JoinGameRequest(validToken2, "WHITE", game.gameID())));
    }

    @Test
    public void joinGameInvalidID() throws  UnauthorizedException, DataAccessException, BadRequestException {
        gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));

        assertThrows(BadRequestException.class, () -> gameService.joinGame(new JoinGameRequest(validToken, "WHITE", 900)));
    }
}
