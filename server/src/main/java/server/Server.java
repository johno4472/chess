package server;

import dataaccess.*;
import service.ChessService;
import spark.*;

import static dataaccess.DatabaseManager.createDatabase;

public class Server {

    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);

        GameDAO gameDAO;
        AuthDAO authDAO;
        UserDAO userDAO;
        //gameDAO = new MemoryGameDAO();
        //authDAO = new MemoryAuthDAO();
        //userDAO = new MemoryUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();
        userDAO = new MySQLUserDAO();
        DatabaseManager.createDatabase();
        ChessService service;
        service = new ChessService(gameDAO, authDAO, userDAO);
        //a. need to initialize user, game, and auth DAO to pass in here

        Spark.staticFiles.location("web");

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
