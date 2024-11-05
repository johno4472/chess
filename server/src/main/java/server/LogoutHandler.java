package server;

import com.google.gson.Gson;
import model.AuthData;
import service.ChessService;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private final ChessService service;

    public LogoutHandler(ChessService service) {
        this.service = service;
    }

    public Object logout (Request req, Response res) {
        AuthData authData = new Gson().fromJson(req.body(), AuthData.class);
        service.deleteAuth(authData);
        return res;
    }

}
