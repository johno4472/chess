package client;

import chess.ChessGame;
import dataaccess.*;
import model.UserData;
import model.requestresult.*;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;
import service.ChessService;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static ServerFacade facade;
    private static Server server;
    private AuthDAO authDAO = new MySQLAuthDAO();
    private GameDAO gameDAO = new MySQLGameDAO();
    private UserDAO userDAO = new MySQLUserDAO();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clear(){
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(new UserData("user", "password", "email"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        var authData = facade.register(new UserData("user", "password", "email"));
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        assertNotNull(response.message());
    }

    @Test
    void login() throws Exception {
        facade.register(new UserData("user", "password", "email"));
        LoginResponse response = facade.login(new LoginRequest("user", "password"));
        assertNotNull(response.username());
    }

    @Test
    void loginNegative() throws Exception {
        LoginResponse response = facade.login(new LoginRequest("user", "password"));
        assertNotNull(response.message());
    }

    @Test
    void createGame() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        assertNotNull(gameDAO.getGame(1));
    }

    @Test
    void createGameNegative() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        assertEquals(gameDAO.listGames().size(), 2);
    }

    @Test
    void listGames() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        facade.createGame(new CreateGameRequest("game2", response.authToken()));
        facade.createGame(new CreateGameRequest("game3", response.authToken()));
        ListGamesResponse listResponse = facade.listGames(new ListGamesRequest(response.authToken()));
        assertEquals(listResponse.games().size(), 3);
    }

    @Test
    void listGamesNegative() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        ListGamesResponse listResponse = facade.listGames(new ListGamesRequest(response.authToken()));
        assertEquals(listResponse.games().size(), 0);
    }

    @Test
    void joinGame() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1, response.authToken()));
        assertEquals(gameDAO.getGame(1).whiteUsername(), "user");
    }

    @Test
    void joinGameNegative() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        JoinGameResponse joinResponse = facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1, response.authToken()));
        assertNotNull(joinResponse.message());
    }

    @Test
    void observeGame() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.createGame(new CreateGameRequest("game", response.authToken()));
        JoinGameResponse observe = facade.observeGame(1, response.authToken());
        assertNull(observe.message());
    }

    @Test
    void observeGameNegative() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        JoinGameResponse observe = facade.observeGame(1, response.authToken());
        assertNotNull(observe.message());
    }

    @Test
    void logout() throws Exception {
        RegisterResponse response = facade.register(new UserData("user", "password", "email"));
        facade.logout(response.authToken());
        assertNull(authDAO.getAuth(response.authToken()));
    }

    @Test
    void logoutNegative() throws Exception {
        facade.logout("a");
    }

}
