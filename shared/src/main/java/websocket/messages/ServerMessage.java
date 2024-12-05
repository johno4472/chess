package websocket.messages;

import chess.ChessBoard;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    ChessBoard game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, ChessBoard game) {
        this.message = message;
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessageBody() {
        return message;
    }

    public String toString(){
        return new Gson().toJson(this);
    }

    public ChessBoard getChessBoard() {
        return game;
    }

    public void nullifyGame() {
        this.game = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
