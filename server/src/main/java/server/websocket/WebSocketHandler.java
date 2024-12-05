package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
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
    private GameData gameData;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.authDAO = new MySQLAuthDAO();
        this.gameDAO = new MySQLGameDAO();
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            this.gameData = gameDAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getAuthToken(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
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
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    username + " has joined game #" + gameID + " as " + textColor + "!",
                    gameDAO.getGame(gameID).game().getBoard(),
                    "You have joined game #" + gameID + " as " + textColor + "!");
            connections.broadcast(username, gameID, serverMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void makeMove(String authToken, Session session){

    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            connections.remove(authToken);
            username = authDAO.getAuth(authToken).username();
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game",
                    new ChessBoard(), "You have left the game");
            connections.broadcast(username, gameID, serverMessage);
            gameData = gameDAO.getGame(gameID);
            if (!(gameData.blackUsername() == null) && gameData.blackUsername().equals(username)){
                gameData = gameData.nullifyBlack();
            } else if (!(gameData.whiteUsername() == null) && gameData.whiteUsername().equals(username)){
                gameData = gameData.nullifyWhite();
            }
            gameDAO.updateGame(gameID, gameData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(String authToken, Session session){

    }

}