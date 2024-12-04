package server.websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private static final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken(), session);
            case LEAVE -> leave(command.getAuthToken(), session);
            case RESIGN -> resign(command.getAuthToken(), session);
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(authToken, new SessionInfo(gameID, (org.glassfish.grizzly.http.server.Session) session));
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                "Player has joined game #" + authToken + "!", new ChessBoard());
        connections.broadcast(authToken, serverMessage);
    }

    private void makeMove(String authToken, Session session){

    }

    private void leave(String authToken, Session session) throws IOException {
        connections.remove("visitorName");
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "This person left the game",
                new ChessBoard());
        connections.broadcast("authToken", notification);
    }

    private void resign(String authToken, Session session){

    }

    public void makeNoise(String petName, String sound) throws Exception {
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "This guy's making noise",
                    new ChessBoard());
            connections.broadcast("authToken", serverMessage);
        } catch (Exception ex) {
            throw new Exception();
        }
    }
}