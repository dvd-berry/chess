package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("Minnish Cap");
        assertNotEquals(0, gameID);
        assertNotNull(gameDAO.getGame(gameID));
    }

    @Test
    public void createGameNullName() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("Skyward Sword");
        assertEquals("Skyward Sword", gameDAO.getGame(gameID).gameName());
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        assertNull(gameDAO.getGame(900));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Windwaker");
        gameDAO.createGame("Breath of the Wild");
        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    public void lisGamesEmpty() throws DataAccessException {
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("Spirit Tracks");
        gameDAO.updateGame(new GameData(gameID, "Link", "Phantom", "Tears of the Kingdom", new ChessGame()));
        assertEquals("Link", gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    public void updateGameNullName() throws DataAccessException {
        int gameID = gameDAO.createGame("Spirit Tracks");
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(new GameData(gameID, "Link", "Phantom", null, new ChessGame())));
    }

    @Test
    public void clearRemovesGames() throws DataAccessException {
        gameDAO.createGame("Majora's Mask");
        gameDAO.createGame("Twilight Princess");
        gameDAO.clear();
        assertEquals(0, gameDAO.listGames().size());
    }
}

