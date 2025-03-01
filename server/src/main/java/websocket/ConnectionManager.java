package websocket;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    // add connection
    public void add(String authToken, Integer gameID, Session session){
        connections.put(authToken, new Connection(gameID, session));
    }

    // remove connection
    public void remove(String authToken){
        connections.remove(authToken);
    }

    // send messages to each connection
    public void broadcast(String authToken, Integer gameID, ServerMessage serverMessage) throws IOException{
        var removeList = new ArrayList<String>();
        for(Map.Entry<String, Connection> entry : connections.entrySet()){
            var auth = entry.getKey();
            var connection = entry.getValue();
            if(connection.session.isOpen()){
                if(!auth.equals(authToken) && Objects.equals(connection.gameID, gameID)){
                    System.out.printf("Sending message to %s%n", auth);
                    connection.send(new Gson().toJson(serverMessage));
                }
            }
            else{
                // remove connection
                removeList.add(auth);
            }
        }
        for(var connection : removeList){
            connections.remove(connection);
        }
    }
}
