package server;

import com.google.gson.Gson;
import model.AuthData;
import service.ChessService;
import model.requestresult.LogoutResponse;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private final ChessService service;

    public LogoutHandler(ChessService service) {
        this.service = service;
    }

    public Object logout (Request req, Response res) {
        String authToken = new Gson().fromJson(req.headers("authorization"), String.class);
        LogoutResponse logoutResponse;
        logoutResponse = service.logout(authToken);
        if (logoutResponse.message() == null){
            res.status(200);
        }
        else{
            res.status(401);
        }
        return new Gson().toJson(logoutResponse);
    }

}
