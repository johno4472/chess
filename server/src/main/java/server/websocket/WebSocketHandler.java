package server.websocket;

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

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getGameID(), session);
            case LEAVE -> leave(command.getGameID(), session);
            case RESIGN -> resign(command.getGameID(), session);
        }
    }

    private void connect(int gameID, Session session) throws IOException {
        connections.add(gameID, session);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(gameID, serverMessage);
    }

    private void makeMove(int gameID, Session session){

    }

    private void leave(int gameID, Session session) throws IOException {
        connections.remove("visitorName");
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(1, notification);
    }

    private void resign(int gameID, Session session){

    }

    public void makeNoise(String petName, String sound) throws Exception {
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            connections.broadcast(1, serverMessage);
        } catch (Exception ex) {
            throw new Exception();
        }
    }
}