package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void message(ServerMessage message, String strMessage);
}
