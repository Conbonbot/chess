package websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.google.gson.Gson;

import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.HighlightCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.RequestBoard;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;


public class WebSocketFacade extends Endpoint{

    private Session session;
    private ServerMessageObserver messageObserver;

    public WebSocketFacade(String url, ServerMessageObserver messageObserver) throws ResponseException{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageObserver = messageObserver;
        
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message){
                    ServerMessage servMessage = new Gson().fromJson(message, ServerMessage.class);
                    messageObserver.message(servMessage, message);
                }
            });
        } 
        catch (IOException | IllegalStateException | URISyntaxException | DeploymentException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session sn, EndpointConfig ec) {
    }

    public void loadBoard(String authToken, String gameID, boolean isWhite) throws Exception{
        var load = new RequestBoard(CommandType.REQUEST_BOARD, authToken, Integer.valueOf(gameID), isWhite);
        this.session.getBasicRemote().sendText(new Gson().toJson(load));
    }

    public void connect(String authToken, String gameID, String strType) throws ResponseException{
        try {
            if(strType.toUpperCase().equals("WHITE") || strType.toUpperCase().equals("BLACK")){
                var connect = new ConnectCommand(CommandType.CONNECT, authToken, Integer.valueOf(gameID), strType.toUpperCase());
                this.session.getBasicRemote().sendText(new Gson().toJson(connect));
            }
            else{
                throw new ResponseException(401, "Error: invalid player color");
            }
        } 
        catch (IOException | NumberFormatException ex) {
            throw new ResponseException(400, "Error: bad request -- invalid game");
        }
    }

    public void makeMove(String authToken, String gameID, boolean isWhite, ChessMove move) throws ResponseException, IOException{
        var moveReq = new MakeMoveCommand(CommandType.MAKE_MOVE, authToken, Integer.valueOf(gameID), isWhite, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(moveReq));
    }

    public void highlight(String authToken, String gameID, ChessPosition pos, boolean isWhite) throws Exception{
        var load = new HighlightCommand(CommandType.HIGHLIGHT, authToken, Integer.valueOf(gameID), pos, isWhite);
        this.session.getBasicRemote().sendText(new Gson().toJson(load));
    }

    public void leave(String authToken, String gameID) throws IOException, ResponseException{
        var leave = new UserGameCommand(CommandType.LEAVE, authToken, Integer.valueOf(gameID));
        this.session.getBasicRemote().sendText(new Gson().toJson(leave));
    }

    public void resign(String authToken, String gameID) throws IOException, ResponseException{
        var resign = new UserGameCommand(CommandType.RESIGN, authToken, Integer.valueOf(gameID));
        this.session.getBasicRemote().sendText(new Gson().toJson(resign));
    }

    public void observe(String authToken, String gameID) throws IOException, ResponseException{
        try{

            var observe = new RequestBoard(CommandType.OBSERVE, authToken, Integer.valueOf(gameID), true);
            this.session.getBasicRemote().sendText(new Gson().toJson(observe));
        }
        catch(NumberFormatException ex){
            throw new ResponseException(400, "Error: bad request");
        }
    }



    
}
