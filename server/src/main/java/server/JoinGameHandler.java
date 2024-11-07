package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.JoinGameOptions;
import service.ChessService;
import service.requestresult.JoinGameRequest;
import service.requestresult.JoinGameResult;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    private final ChessService service;

    public JoinGameHandler(ChessService service) {
        this.service = service;
    }

    public Object joinGame (Request req, Response res) {
        String authToken = req.headers("authorization");
        JoinGameOptions options = new Gson().fromJson(req.body(), JoinGameOptions.class);

        if (options.playerColor() != ChessGame.TeamColor.BLACK && options.playerColor() != ChessGame.TeamColor.WHITE){
            res.status(400);
            return new Gson().toJson(new JoinGameResult("Error: bad color request"));
        }

        JoinGameResult joinGameResult;
        joinGameResult = service.joinGame(new JoinGameRequest(options.playerColor(), options.gameID(), authToken));

        String message = joinGameResult.message();
        switch (message) {
            case "Error: bad request":
                res.status(400);
                break;
            case "Error: unauthorized":
                res.status(401);
                break;
            case "Error: already taken":
                res.status(403);
                break;
            case null:
                res.status(200);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message);
        }
        return new Gson().toJson(joinGameResult);
    }
}
