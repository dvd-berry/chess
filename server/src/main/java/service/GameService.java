package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;


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

    public ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedException, DataAccessException {
        authorize(request.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }

    public void joinGame(JoinGameRequest request)
            throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        String username = authorize(request.authToken()).username();

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        GameData updated;
        if("WHITE".equals(request.playerColor())) {
            if(game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else if("BLACK".equals(request.playerColor())) {
            if(game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
            updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new BadRequestException("Error: bad request");
        }

        gameDAO.updateGame(updated);

    }

    private AuthData authorize(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData auth = authToken == null ? null : authDAO.getAuth(authToken);
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return auth;
    }
}
