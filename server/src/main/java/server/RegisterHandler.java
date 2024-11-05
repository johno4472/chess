package server;

import com.google.gson.Gson;
import model.UserData;
import service.ChessService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class RegisterHandler {

    private final ChessService service;

    public RegisterHandler(ChessService service) {
        this.service = service;
    }

    public Object register (Request req, Response res) {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        service.addUser(userData);
        return res;
    }
}
