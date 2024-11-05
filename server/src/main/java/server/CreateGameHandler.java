package server;

import service.ChessService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class CreateGameHandler {

    private final ChessService service;

    public CreateGameHandler(ChessService service) {
        this.service = service;
    }

    public Object createGame (Request req, Response res) {
        var gameID = new Gson().fromJson(req.body(), int.class);
        service.createGame(gameID);

        return gameID;
    }
}
