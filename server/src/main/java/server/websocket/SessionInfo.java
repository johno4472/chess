package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

public record SessionInfo(int gameID, Session session, String username) {
}