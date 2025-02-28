package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String authToken;
    public Integer gameID;
    public Session session;

    public Connection(String authToken, Integer gameID, Session session){
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }

    public Integer getGameID(){
        return gameID;
    }

    public String getAuthToken(){
        return authToken;
    }

    public void send(String msg) throws IOException{
        session.getRemote().sendString(msg);
    }
}
