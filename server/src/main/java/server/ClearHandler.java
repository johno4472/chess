package server;

import spark.Request;
import spark.Response;

public class ClearHandler {

    public Object clear (Request req, Response res) {
        //call my service layer
        service.clear()
        //which can be another class with a clear database method
        //which can call the "clear all" method in my DataAccess Class, which clears everything
        //better to have 3 different interfaces like in Spec
        //all three interfaces have clear function in them

    }
}
