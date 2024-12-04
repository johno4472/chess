package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
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
    private String username;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private int gameID;
    private ChessGame.TeamColor color;
    private String textColor;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.authDAO = new MySQLAuthDAO();
        this.gameDAO = new MySQLGameDAO();
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken(), session);
            case LEAVE -> leave(command.getAuthToken(), session);
            case RESIGN -> resign(command.getAuthToken(), session);
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        this.gameID = gameID;
        username = authDAO.getAuth(authToken).username();
        try{
            String white = gameDAO.getGame(gameID).whiteUsername();
            String black = gameDAO.getGame(gameID).blackUsername();
            if (black != null && black.equals(username)){
                color = ChessGame.TeamColor.BLACK;
                textColor = "black";
            }
            else if (white != null && white.equals(username)){
                color = ChessGame.TeamColor.WHITE;
                textColor = "white";
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        connections.add(authToken, new SessionInfo(gameID, session, username));
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                username + " has joined game #" + gameID + " as " + textColor + "!", new ChessBoard(),
                "You have joined game #" + gameID + " as " + textColor + "!");
        connections.broadcast(username, gameID, serverMessage);
    }

    private void makeMove(String authToken, Session session){

    }

    private void leave(String authToken, Session session) throws IOException {
        connections.remove("visitorName");
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game",
                new ChessBoard(), "You have left the game");
        connections.broadcast(username, gameID, serverMessage);
    }

    private void resign(String authToken, Session session){

    }
}