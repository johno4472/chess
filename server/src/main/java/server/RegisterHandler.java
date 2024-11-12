package server;

import com.google.gson.Gson;
import model.UserData;
import service.ChessService;
import service.requestresult.RegisterResult;
import spark.Request;
import spark.Response;
import model.requestresult.RegisterResult;

public class RegisterHandler {

    private final ChessService service;

    public RegisterHandler(ChessService service) {
        this.service = service;
    }

    public Object register (Request req, Response res) {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        if (userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            return new Gson().toJson(new RegisterResult(null, null, "Error: bad request"));
        }
        RegisterResult registerResult;
        registerResult = service.addUser(userData);
        if (registerResult.username() != null){
            res.status(200);
        }
        else{
            res.status(403);
        }
        return new Gson().toJson(registerResult);
    }
}
