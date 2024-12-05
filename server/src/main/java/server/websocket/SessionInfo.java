package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public record SessionInfo(int gameID, Session session, String username, ChessGame.TeamColor color) {
}
