package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterResult result = userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));

        assertEquals("Link", result.username());
        assertNotNull(result.authToken());
        assertNotNull(userDAO.getUser("Link"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerUsernameTaken() throws Exception {
        userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));
        assertThrows(AlreadyTakenException.class, () -> userService.register(new RegisterRequest("Link", "other", "other@triforce.com")));
    }

    @Test
    public void registerMissingField() {
        assertThrows(BadRequestException.class, () ->
                userService.register(new RegisterRequest("Link", null, "link@triforce.com")));
    }

}
