package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SQLAuthDAOTest {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokendorf", "Link"));

        assertNotNull(authDAO.getAuth("tokendorf"));
    }

    @Test
    public void createAuthDuplicateToken() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokendorf", "Link"));

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(new AuthData("tokendorf", "Link")));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokendorf", "Link"));

        assertEquals("Link", authDAO.getAuth("tokendorf").username());
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        assertNull(authDAO.getAuth("tokendorf"));
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokendorf", "Link"));
        authDAO.deleteAuth("tokendorf");

        assertNull(authDAO.getAuth("tokendorf"));
    }

    @Test
    public void deleteAuthNotFound() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("tokendorf"));
    }

    @Test
    public void clearRemovesAuths() throws DataAccessException {
        authDAO.createAuth(new AuthData("tokendorf", "Link"));
        authDAO.createAuth(new AuthData("tokarina-of-time", "Navi"));
        authDAO.clear();

        assertNull(authDAO.getAuth("tokendorf"));
        assertNull(authDAO.getAuth("tokarina-of-time"));

    }
}
