package dataaccess;

import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("Link", "zelda<3", "link@triforce.com"));

        assertNotNull(userDAO.getUser("Link"));
    }

    @Test
    public void createUserUsernameTaken() throws DataAccessException {
        userDAO.createUser(new UserData("Link", "zelda<3", "link@triforce.com"));

        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("Link", "kokoriko", "link@triforce.org")));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("Link", "zelda<3", "link@triforce.com"));

        assertEquals("Link", userDAO.getUser("Link").username());
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        assertNull(userDAO.getUser("Link"));
    }

    @Test
    public void clearRemovesUsers() throws DataAccessException {
        userDAO.createUser(new UserData("Link", "zelda<3", "link@triforce.com"));
        userDAO.clear();

        assertNull(userDAO.getUser("Link"));
    }
}
