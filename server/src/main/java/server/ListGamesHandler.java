package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ChessService;
import model.requestresult.ListGamesResponse;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    private final ChessService service;

    public ListGamesHandler(ChessService service) {
        this.service = service;
    }

    public Object listGames (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        ListGamesResponse listGamesResponse;
        listGamesResponse = service.listGames(authToken);

        if (listGamesResponse.games() != null){
            res.status(200);
        }
        else{
            res.status(401);
        }
        return new Gson().toJson(listGamesResponse);
    }
}
