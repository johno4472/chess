package server;

import com.google.gson.Gson;
import model.UserData;
import service.ChessService;
import model.requestresult.RegisterResponse;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private final ChessService service;

    public RegisterHandler(ChessService service) {
        this.service = service;
    }

    public Object register (Request req, Response res) {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        if (userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            return new Gson().toJson(new RegisterResponse(null, null, "Error: bad request"));
        }
        RegisterResponse registerResponse;
        registerResponse = service.addUser(userData);
        if (registerResponse.username() != null){
            res.status(200);
        }
        else{
            res.status(403);
        }
        return new Gson().toJson(registerResponse);
    }
}
