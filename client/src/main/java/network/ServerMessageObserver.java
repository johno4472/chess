package network;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage serverMessage);
}