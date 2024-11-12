package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.requestresult.*;

import static org.junit.jupiter.api.Assertions.*;

class ChessServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ChessService service;

    @BeforeEach
    void initialize(){
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new ChessService(gameDAO, authDAO, userDAO);
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

    @Test
    void clear() {
        userDAO.addUser(new UserData("user", "password", "email"));
        service.clear();
        assertNull(userDAO.getUser("user"));
    }

    @Test
    void loginPositive() {
        UserData user = new UserData("user", "password", "email");
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        userDAO.addUser(new UserData("user", hashedPassword, "email"));
        LoginResult result = service.login(new LoginRequest("user", "password"));
        assertEquals(result.username(), "user");
    }

    @Test
    void loginNegative() {
        UserData user = new UserData("user", "password", "email");
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        userDAO.addUser(new UserData("user", hashedPassword, "email"));
        LoginResult result = service.login(new LoginRequest("user", "wrongPassword"));
        assertEquals(result.message(), "Error: unauthorized");
    }

    @Test
    void addUserPositive() {
        UserData userData;
        userData = new UserData("user", "password", "email");
        service.addUser(userData);
        assertEquals(userDAO.getUser("user"), userData);
    }

    @Test
    void addUserNegative() {
        userDAO.addUser(new UserData("user", "password", "email"));
        RegisterResult result = service.addUser(new UserData("user", "password1", "email1"));
        assertEquals(result.message(), "Error: already taken");
    }

    @Test
    void logoutPositive() {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        service.logout("authToken");
        assertNull(authDAO.getAuth("authToken"));
    }

    @Test
    void logoutNegative() {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        LogoutResponse response = service.logout("wrongAuthToken");
        assertEquals(response.message(), "Error: unauthorized");
    }

    @Test
    void createGamePositive() throws DataAccessException {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        service.createGame(new CreateGameRequest("gameName", "authToken"));
        assertEquals(gameDAO.listGames().size(), 1);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        CreateGameResult result = service.createGame(new CreateGameRequest("gameName",
                "wrongAuthToken"));
        assertEquals(result.message(), "Error: unauthorized");
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        service.createGame(new CreateGameRequest("gameName", "authToken"));
        service.createGame(new CreateGameRequest("gameName", "authToken"));
        ListGamesResult listGamesResult = service.listGames("authToken");
        assertEquals(listGamesResult.games().size(), 2);
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        authDAO.createAuth("authToken", new AuthData("authToken", "user"));
        service.createGame(new CreateGameRequest("gameName", "authToken"));
        service.createGame(new CreateGameRequest("gameName", "authToken"));
        ListGamesResult listGamesResult = service.listGames("wrongAuthToken");
        assertEquals(listGamesResult.message(), "Error: unauthorized");
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        authDAO.createAuth("authToken1", new AuthData("authToken1", "user1"));
        CreateGameResult result = service.createGame(new CreateGameRequest("gameName",
                "authToken1"));
        int gameID = result.gameID();
        authDAO.createAuth("authToken2", new AuthData("authToken2", "user2"));
        service.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID, "authToken1"));
        service.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID, "authToken2"));
        assertEquals(gameDAO.getGame(gameID).whiteUsername(), "user1");
        assertEquals(gameDAO.getGame(gameID).blackUsername(), "user2");
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        authDAO.createAuth("authToken1", new AuthData("authToken1", "user1"));
        CreateGameResult createResult = service.createGame(new CreateGameRequest("gameName",
                "authToken1"));
        int gameID = createResult.gameID();
        authDAO.createAuth("authToken2", new AuthData("authToken2", "user2"));
        service.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID, "authToken1"));
        JoinGameResult joinResult = service.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID,
                "authToken2"));
        assertEquals(gameDAO.getGame(gameID).whiteUsername(), "user1");
        assertEquals(joinResult.message(), "Error: already taken");
    }
}