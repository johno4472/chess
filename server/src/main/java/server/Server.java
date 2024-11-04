package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        ClearHandler clearHandler = new ClearHandler();
        Spark.delete("/db", clearHandler::clear);

        RegisterHandler registerHandler = new RegisterHandler();
        Spark.post("/user", registerHandler::register);

        LoginHandler loginHandler = new LoginHandler();
        Spark.post("/session", loginHandler::login);

        LogoutHandler logoutHandler = new LogoutHandler();
        Spark.delete("/session", logoutHandler::logout);

        ListGamesHandler listGamesHandler = new ListGamesHandler();
        Spark.get("/game", listGamesHandler::listGames);

        CreateGameHandler createGameHandler = new CreateGameHandler();
        Spark.post("/game", createGameHandler::createGame);

        JoinGameHandler joinGame = new JoinGameHandler();
        Spark.put("/game", createGameHandler::createGame);


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
