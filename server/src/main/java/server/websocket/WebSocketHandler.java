package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;


@WebSocket
public class WebSocketHandler {

    private static final ConnectionManager CONNECTIONS = new ConnectionManager();
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

        if (authDAO.getAuth(authToken) == null){
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
            connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                    null, null, "Invalid AuthToken").toString());
            return;
        }

        username = authDAO.getAuth(authToken).username();
        try{


            if (gameID > gameDAO.listGames().size() || gameID < 0) {
                Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                        null, null, "Invalid Game #").toString());
                return;
            }
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
        CONNECTIONS.add(authToken, new SessionInfo(gameID, session, username, color));
        try {
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined game #" + gameID + " as " + textColor + "!",
                    null, null, null);
            CONNECTIONS.broadcast(username, gameID, serverMessage);
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    null, gameDAO.getGame(gameID).game(), color, null);
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

    private ChessGame.TeamColor opposite(ChessGame.TeamColor color) {
        if (color.equals(ChessGame.TeamColor.WHITE)){
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void makeMove(String authToken, int gameID, Session session, ChessMove chessMove){
        try {
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));

            if (authDAO.getAuth(authToken) == null){
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                        null, null, "Invalid AuthToken").toString());
                return;
            }
            username = authDAO.getAuth(authToken).username();
            gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            color = getColor(username, gameData);

            if (game.getIsOver()) {
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, null,
                        "Game is over! No more actions to alter the board").toString());
                return;
            }

            if (chessMove == null) {
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        null, game, color, null).toString());
                return;
            } else if (chessMove.getEndPosition() == null) {
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        "highlight", game, color, chessMove.getStartPosition().getRowCol()).toString());
                return;
            }
            if (!game.getTeamTurn().equals(color)){
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                        null, null, "Hey! It's not your turn yet!").toString());
                return;
            }
            ChessPiece piece = game.getBoard().getPiece(chessMove.getStartPosition());
            if (piece == null || piece.getTeamColor() != color){
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null,
                        null, "Hey! You need to select a " +
                        "square with one of YOUR pieces on it.").toString());
                return;
            }

            if (!game.isIn(chessMove, game.validMoves(chessMove.getStartPosition()))){
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                        null, null, "Hey! That doesn't look like" +
                        "a valid move! Maybe look at the valid moves for each piece and choose one of those options.").toString());
                return;
            }

            ArrayList<String> moveArray = customizeMoveMessage(game.getBoard(), chessMove);
            game.makeMove(chessMove);
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(gameID, gameData);

            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game, color, null);
            CONNECTIONS.broadcast(username, gameID, serverMessage);
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game,
                    color, null);
            connection.send(serverMessage.toString());
            CONNECTIONS.broadcast(username, gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " moved " + moveArray.get(0) + " from " + moveArray.get(1) +
                            " to " + moveArray.get(2), null, null, null));

            if (game.isInCheckmate(opposite(color))){
                ServerMessage checkMateMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "" + opposite(color).toString() + " is in checkmate. " + username
                                + " wins", null, null, null);
                CONNECTIONS.broadcast(username, gameID, checkMateMessage);
                connection.send(checkMateMessage.toString());
                game.updateToOver();
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                        gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(gameID, gameData);
            }

            else if (game.isInCheck(opposite(color))){
                ServerMessage checkMateMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "" + opposite(color).toString() + "is in check. " + username, null, null,
                        null);
                connection.send(checkMateMessage.toString());
                game.updateToOver();
            }
            else if (game.isInStalemate(opposite(color))){
                ServerMessage checkMateMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Game is in stalemate. It's a tie!", null, null, null);
                connection.send(checkMateMessage.toString());
                game.updateToOver();
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                        gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(gameID, gameData);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            CONNECTIONS.remove(authToken);
            username = authDAO.getAuth(authToken).username();
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game",
                    null, null, null);
            CONNECTIONS.broadcast(username, gameID, serverMessage);
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
            CONNECTIONS.remove(authToken);
            username = authDAO.getAuth(authToken).username();
            if (getColor(username, gameDAO.getGame(gameID)) == null){
                Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, null,
                        "You are an observer! You can't resign. You may leave if you wish").toString());
                return;
            }
            if (gameDAO.getGame(gameID).game().getIsOver()){
                Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
                connection.send(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, null,
                        "Game is over! Why are you trying to resign?").toString());
                return;
            }
            var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                    " has resigned. " + getOpponentName(username, gameID) + " has won!",
                    null, null, null);
            CONNECTIONS.broadcast(username, gameID, serverMessage);
            serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "You gave up and lost. I hope you're happy", null, null, null);
            Connection connection = new Connection(authToken, new SessionInfo(gameID, session, username, color));
            connection.send(serverMessage.toString());
            gameData = gameDAO.getGame(gameID).updateToOver();
            gameDAO.updateGame(gameID, gameData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}