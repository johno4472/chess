package server;

import dataaccess.DataAccessException;
import model.GameData;
import service.ChessService;
import model.requestresult.CreateGameRequest;
import model.requestresult.CreateGameResponse;
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

        CreateGameResponse createGameResponse;
        createGameResponse = service.createGame(new CreateGameRequest(gameName, authToken));

        if (createGameResponse.gameID() == null){
            res.status(401);
        }
        else {
            res.status(200);
        }
        return new Gson().toJson(createGameResponse);
//        return new Gson().toJson(createGameResult);
    }
}
