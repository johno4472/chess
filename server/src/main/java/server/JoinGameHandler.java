package server;

import com.google.gson.Gson;
import model.GameData;
import service.ChessService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    private final ChessService service;

    public JoinGameHandler(ChessService service) {
        this.service = service;
    }

    public Object joinGame (Request req, Response res) {
        GameData gameStuff = new Gson().fromJson(req.body(), GameData.class);
        service.updateGame(gameStuff.gameID());

        return res;
    }
}
