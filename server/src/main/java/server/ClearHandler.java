package server;

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
        //all three interfaces have clear function in them
        return null;
    }
}
