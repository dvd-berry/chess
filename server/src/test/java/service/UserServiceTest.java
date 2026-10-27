package service;

import dataaccess.*;
import org.eclipse.jetty.util.log.Log;
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

    @Test
    public void loginSuccess() throws Exception {
        userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));

        LoginResult result = userService.login(new LoginRequest("Link", "zelda<3"));

        assertEquals("Link", result.username());
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void loginWrongPassword() throws Exception {
        userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));

        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("Link", "goron<3")));
    }

    @Test
    public void loginWrongUsername() throws Exception {
        userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));
        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("Saria", "Kokiri")));

    }

    @Test
    public void loginNullParameter() throws Exception {
        userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));
        assertThrows(BadRequestException.class, () -> userService.login(new LoginRequest(null, "Skyloft")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        RegisterResult result = userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));
        userService.logout(new LogoutRequest(result.authToken()));

        assertNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void logoutInvalidToken() throws Exception {
        RegisterResult result = userService.register(new RegisterRequest("Link", "zelda<3", "link@triforce.com"));

        assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest("Tingle-token")));
    }

}