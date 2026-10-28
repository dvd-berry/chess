package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;


public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request)
        throws UnauthorizedException, BadRequestException, DataAccessException {
        authorize(request.authToken());

        if(request.gameName() == null) {
            throw new BadRequestException(("Error: bad request"));
        }

        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);

    }

    private void authorize(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
