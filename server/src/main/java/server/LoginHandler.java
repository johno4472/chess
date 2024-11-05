package server;

import service.ChessService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private final ChessService service;

    public LoginHandler(ChessService service) {
        this.service = service;
    }

    public Object login (Request req, Response res) {
        service.createAuth();
        return res;
    }
}
