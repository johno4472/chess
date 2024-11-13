package server;

import com.google.gson.Gson;
import model.requestresult.ClearResponse;
import spark.Request;
import spark.Response;
import service.ChessService;

public class ClearHandler {

    private final ChessService service;

    public ClearHandler(ChessService service) {
        this.service = service;
    }

    public Object clear (Request req, Response res) {
        //call my service layer
        //which can be another class with a clear database method
        //which can call the "clear all" method in my DataAccess Class, which clears everything
        //better to have 3 different interfaces like in Spec
        service.clear();
        res.status(200);
        //all three interfaces have clear function in them
        ClearResponse clearResponse = new ClearResponse(null);
        return new Gson().toJson(clearResponse);
    }
}
