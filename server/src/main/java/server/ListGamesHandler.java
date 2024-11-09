package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ChessService;
import service.requestresult.ListGamesResult;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    private final ChessService service;

    public ListGamesHandler(ChessService service) {
        this.service = service;
    }

    public Object listGames (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        ListGamesResult listGamesResult;
        listGamesResult = service.listGames(authToken);

        if (listGamesResult.games() != null){
            res.status(200);
        }
        else{
            res.status(401);
        }
        return new Gson().toJson(listGamesResult);
    }
}
