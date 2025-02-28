package websocket;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    // add connection
    public void add(String authToken, Session session){
        // TODO: change
        connections.put(authToken, new Connection(authToken, 0, session));
    }

    // remove connection
    public void remove(String authToken){
        connections.remove(authToken);
    }

    // send messages to each connection
    public void broadcast(String authToken, ServerMessage serverMessage) throws IOException{
        var removeList = new ArrayList<Connection>();
        for(var connection : connections.values()){
            if(connection.session.isOpen()){
                if(!connection.getGameID().equals(authToken)){
                    connection.send(serverMessage.toString());
                }
            }
            else{
                removeList.add(connection);
            }
        }
        for(var connection : removeList){
            connections.remove(connection.getGameID());
        }
    }
}
