package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, SessionInfo sessionInfo) {
        var connection = new Connection(authToken, sessionInfo);
        connections.put(authToken, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String authToken, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        String username = "Test";
        int size = connections.size();
        System.out.println("connections size -> " + size);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (username.equals("Test")) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.gameID);
        }
    }
}