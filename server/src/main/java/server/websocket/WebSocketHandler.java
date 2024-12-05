package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
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
            case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), session, command.getChessMove());
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
            else {
                color = null;
                textColor = "an observer";
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        connections.add(authToken, new SessionInfo(gameID, session, username));
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined game #" + gameID + " as " + textColor + "!",
                    null, null);
            connections.broadcast(username, gameID, serverMessage);
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username));
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    null,
                    gameDAO.getGame(gameID).game().getBoard(), color);
            connection.send(serverMessage.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> customizeMoveMessage(ChessBoard board, ChessMove move) {
        ArrayList<String> moveArray = new ArrayList<>();
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        moveArray.add(piece.getPieceType().toString());
        char[] alphabet = "abcdefgh".toCharArray();
        moveArray.add("" + start.getRow() + alphabet[start.getColumn() - 1]);
        ChessPosition end = move.getEndPosition();
        moveArray.add("" + end.getRow() + alphabet[end.getColumn() - 1]);
        return moveArray;
    }

    public ChessGame.TeamColor getColor(String username, GameData gameData) {
        if (username.equals(gameData.blackUsername())){
            return ChessGame.TeamColor.BLACK;
        } else if (username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return null;
        }
    }

    private void makeMove(String authToken, int gameID, Session session, ChessMove chessMove){
        try {
            username = authDAO.getAuth(authToken).username();
            gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            color = getColor(username, gameData);
            if (!game.getTeamTurn().equals(color)){
                Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username));
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Hey! It's not your turn yet!",
                        null, null).toString());
                return;
            }
            ArrayList<String> moveArray = customizeMoveMessage(game.getBoard(), chessMove);
            game.makeMove(chessMove);
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(gameID, gameData);
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, username + " moved " +
                    moveArray.get(0) + " from " + moveArray.get(1) + " to " + moveArray.get(2), game.getBoard(), color);
            connections.broadcast(username, gameID, serverMessage);
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game.getBoard(), color);
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username));
            connection.send(serverMessage.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            connections.remove(authToken);
            username = authDAO.getAuth(authToken).username();
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game",
                    null, null);
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

    private String getOpponentName(String username, int gameID) {
        try {
            String blackUser = gameDAO.getGame(gameID).blackUsername();
            String whiteUser = gameDAO.getGame(gameID).blackUsername();
            if (!(blackUser == null) && blackUser.equals(username)) {
                if (whiteUser != null) {
                    return whiteUser;
                }
            } else if (!(whiteUser == null) && whiteUser.equals(username)) {
                if (blackUser != null) {
                    return whiteUser;
                }
            }
            return "The Ghost Player";

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData updateToOver(GameData gameData) {
        return gameData.updateToOver();
    }

    private void resign(String authToken, Session session){
        try {
            connections.remove(authToken);
            username = authDAO.getAuth(authToken).username();
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                    " has resigned. " + getOpponentName(username, gameID) + " has won!",
                    null, null);
            connections.broadcast(username, gameID, serverMessage);
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "You gave up and lost. I hope you're happy", null, null);
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username));
            connection.send(serverMessage.toString());
            gameData = gameDAO.getGame(gameID).updateToOver();
            gameDAO.updateGame(gameID, gameData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}