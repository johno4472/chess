package server;

import com.google.gson.Gson;
import service.ChessService;
import model.requestresult.LoginRequest;
import model.requestresult.LoginResponse;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private final ChessService service;

    public LoginHandler(ChessService service) {
        this.service = service;
    }

    public String login (Request req, Response res) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        //c. call a method in my service, which checks if username exists then if that
        // and password match
        //d. Service will call a dataaccess method to create an Authtoken
        //e. Dataaccess returns authtoken to service, which returns to handler, which creates
        // a loginresponse object with the username and authtoken and message
        LoginResponse loginResponse;
        loginResponse = service.login(loginRequest);
        // that gets sent to my server
        if (loginResponse.username() != null){
            res.status(200);
        }
        else {
            res.status(401);
        }
        //res.status(); // <- this is the status
        //make login result in service and send to handler
        return new Gson().toJson(loginResponse);
    }
}
