package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public Integer gameID;
    public Session session;

    public Connection(Integer gameID, Session session){
        this.gameID = gameID;
        this.session = session;
    }

    public Integer getGameID(){
        return gameID;
    }

    public void send(String msg) throws IOException{
        session.getRemote().sendString(msg);
    }
}
