package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public int gameID;
    public SessionInfo sessionInfo;
    public Session session;
    public String authToken;

    public Connection(String authToken, SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.session = (Session) sessionInfo.session();
        this.gameID = sessionInfo.gameID();
        this.authToken = authToken;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
