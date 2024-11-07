package server;

import com.google.gson.Gson;
import service.requestresult.ClearResult;
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
        ClearResult clearResult = new ClearResult(null);
        return new Gson().toJson(clearResult);
    }
}
