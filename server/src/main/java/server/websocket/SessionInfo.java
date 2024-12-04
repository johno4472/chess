package server.websocket;

import org.glassfish.grizzly.http.server.Session;

public record SessionInfo(int gameID, Session session) {
}
