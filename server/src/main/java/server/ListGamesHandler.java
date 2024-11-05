package server;

import service.ChessService;
import spark.Request;
import spark.Response;

public class ListGamesHandler {

    private final ChessService service;

    public ListGamesHandler(ChessService service) {
        this.service = service;
    }

    public Object listGames (Request req, Response res) {
        service.listGames();
        return res;
    }
}
