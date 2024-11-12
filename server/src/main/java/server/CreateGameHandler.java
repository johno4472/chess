package server;

import dataaccess.DataAccessException;
import model.GameData;
import service.ChessService;
import model.requestresult.CreateGameRequest;
import model.requestresult.CreateGameResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class CreateGameHandler {

    private final ChessService service;

    public CreateGameHandler(ChessService service) {
        this.service = service;
    }

    public Object createGame (Request req, Response res) throws DataAccessException {
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);
        String authToken = new Gson().fromJson(req.headers("authorization"), String.class);
        String gameName = gameData.gameName();

        CreateGameResult createGameResult;
        createGameResult = service.createGame(new CreateGameRequest(gameName, authToken));

        if (createGameResult.gameID() == null){
            res.status(401);
        }
        else {
            res.status(200);
        }
        return new Gson().toJson(createGameResult);
//        return new Gson().toJson(createGameResult);
    }
}
