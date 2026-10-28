package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameDAO gameDAO;
    private GameService gameService;
    private final String validToken = "Tokendorf";

    @BeforeEach
    public void setUp() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData(validToken, "Link"));
    }

    @Test
    public void createGameSuccess() throws UnauthorizedException, BadRequestException, DataAccessException {
        CreateGameResult game = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));

        assertNotNull(gameDAO.getGame(game.gameID()));
    }

    @Test
    public void createGameInvalidToken() throws UnauthorizedException, BadRequestException, DataAccessException {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(new CreateGameRequest("Groose-Token", "Four Swords")));
    }

    @Test
    public void createGameMissingName() throws UnauthorizedException, BadRequestException, DataAccessException {
        assertThrows(BadRequestException.class, () -> gameService.createGame(new CreateGameRequest(validToken, null)));
    }

    @Test
    public void listGamesSuccess() throws UnauthorizedException, DataAccessException, BadRequestException {
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));
        CreateGameResult game2 = gameService.createGame(new CreateGameRequest(validToken, "Majora's Mask"));

        ListGamesResult result = gameService.listGames(new ListGamesRequest(validToken));

        assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesInvalidToken() throws UnauthorizedException, DataAccessException, BadRequestException {
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(validToken, "Four Swords"));
        CreateGameResult game2 = gameService.createGame(new CreateGameRequest(validToken, "Majora's Mask"));

        assertThrows(UnauthorizedException.class, () -> gameService.listGames(new ListGamesRequest("LLort-token")));
    }
}
