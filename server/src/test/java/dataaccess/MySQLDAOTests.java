package dataaccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ChessService;
import service.requestresult.CreateGameRequest;
import service.requestresult.RegisterResult;


import static org.junit.jupiter.api.Assertions.*;

public class MySQLDAOTests {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ChessService service;

    @BeforeEach
    public void initialize() {
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();
        service = new ChessService(gameDAO, authDAO, userDAO);
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void clearUser() {
        service.addUser(new UserData("user", "password", "email"));
        userDAO.clear();
        assertNull(userDAO.getUser("user"));
    }

    @Test
    public void addUserPositive() {
        userDAO.addUser(new UserData("user", "password", "email"));
        UserData user = userDAO.getUser("user");
        assertEquals("email", user.email());
    }

    @Test
    public void addUserNegative() {
        userDAO.addUser(new UserData("user", "password", "email"));
        assertThrows(RuntimeException.class,
                () -> userDAO.addUser(new UserData("user", "p", "e")));
    }

    @Test
    public void getUserPositive() {
        userDAO.addUser(new UserData("user", "password", "email"));
        UserData user = userDAO.getUser("user");
        assertEquals("email", user.email());
    }

    @Test
    public void getUserNegative() {
        userDAO.addUser(new UserData("user", "password", "email"));
        UserData user = userDAO.getUser("wrongUser");
        assertNull(user);
    }

    @Test
    public void clearAuth() {
        authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user"));
        authDAO.clear();
        assertNull(authDAO.getAuth("heeby-jeebies"));
    }

    @Test
    public void createAuthPositive() {
        authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user"));
        assertEquals(authDAO.getAuth("heeby-jeebies").username(), "user");

    }

    @Test
    public void createAuthNegative() {
        authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user"));
        assertThrows(RuntimeException.class,
                () -> authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user2")));
    }

    @Test
    public void getAuthPositive() {
        authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user"));
        assertEquals("user", authDAO.getAuth("heeby-jeebies").username());
    }

    @Test
    public void getAuthNegative() {
        assertNull(authDAO.getAuth("heeby-jeebies"));
    }

    @Test
    public void deleteAuthPositive() {
        authDAO.createAuth("heeby-jeebies", new AuthData("heeby-jeebies", "user"));
        authDAO.deleteAuth("heeby-jeebies");
        assertNull(authDAO.getAuth("heeby-jeebies"));
    }

    @Test
    public void deleteAuthNegative() {
        authDAO.deleteAuth("heeby-jeebies");
        assertNull(authDAO.getAuth("heeby-jeebies"));
    }

    @Test
    public void clearGame() {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        gameDAO.clear();
        try {
            assertNull(gameDAO.getGame(1));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void createGamePositive() {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        try {
            assertEquals("game", gameDAO.getGame(1).gameName());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGameNegative() {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        service.createGame(new CreateGameRequest("game", auth));
        try {
            assertNotNull(gameDAO.getGame(2));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        assertEquals("game", gameDAO.getGame(1).gameName());

    }

    @Test
    public void getGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(1));
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        ChessGame game = gameDAO.getGame(1).game();
        gameDAO.updateGame(1, new GameData(1, null, "black", "game", game));
        assertEquals("black", gameDAO.getGame(1).blackUsername());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        ChessGame game = gameDAO.getGame(1).game();
        gameDAO.updateGame(1, new GameData(1, null, null, "game", game));
        assertNull(gameDAO.getGame(1).blackUsername());
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        RegisterResult result = service.addUser(new UserData("test", "test", "test"));
        String auth = result.authToken();
        service.createGame(new CreateGameRequest("game", auth));
        service.createGame(new CreateGameRequest("game", auth));
        service.createGame(new CreateGameRequest("game", auth));
        service.createGame(new CreateGameRequest("game", auth));
        assertEquals(4, gameDAO.listGames().size());
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        assertEquals(0, gameDAO.listGames().size());
    }
}

