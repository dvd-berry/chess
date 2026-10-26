package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void clearRemovesAllData() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("Link", "zelda<3", "link@triforce.com"));
        authDAO.createAuth(new AuthData("Tokendorf", "Link"));
        int gameID = gameDAO.createGame("Hylian Chess");

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        assertNull(userDAO.getUser("Link"));
        assertNull(authDAO.getAuth("Tokendorf"));
        assertNull(gameDAO.getGame(gameID));
    }

}
