package server;

import dataaccess.*;
import server.websocket.WebSocketHandler;
import service.ChessService;
import spark.*;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.createDatabase;

public class Server {
    private final WebSocketHandler webSocketHandler;

    public Server() {
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        GameDAO gameDAO;
        AuthDAO authDAO;
        UserDAO userDAO;
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        ChessService service;
        service = new ChessService(gameDAO, authDAO, userDAO);
        //a. need to initialize user, game, and auth DAO to pass in here

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.

        ClearHandler clearHandler = new ClearHandler(service);
        Spark.delete("/db", clearHandler::clear);

        RegisterHandler registerHandler = new RegisterHandler(service);
        Spark.post("/user", registerHandler::register);

        LoginHandler loginHandler = new LoginHandler(service);
        //b. so I can pass in the service object here ^^
        Spark.post("/session", loginHandler::login);

        LogoutHandler logoutHandler = new LogoutHandler(service);
        Spark.delete("/session", logoutHandler::logout);

        ListGamesHandler listGamesHandler = new ListGamesHandler(service);
        Spark.get("/game", listGamesHandler::listGames);

        CreateGameHandler createGameHandler = new CreateGameHandler(service);
        Spark.post("/game", createGameHandler::createGame);

        JoinGameHandler joinGameHandler = new JoinGameHandler(service);
        Spark.put("/game", joinGameHandler::joinGame);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
