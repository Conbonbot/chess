package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import exception.ResponseException;
import requests.Request;
import service.ChessService;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

@WebSocket
public class WebSocketHandler{

    private final ChessService chessService;

    public WebSocketHandler(ChessService chessService){
        this.chessService = chessService;
    }

    // TODO: for later
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException{
        UserGameCommand commandMessage = new Gson().fromJson(message, UserGameCommand.class);
        switch(commandMessage.getCommandType()){
            case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case LEAVE -> leave(commandMessage, session);
            case RESIGN -> resign(commandMessage, session);
            default -> throw new IOException("Invalid");
        }
    }

    private void connect(ConnectCommand command, Session session) throws IOException, ResponseException{
        connections.add(command.getAuthToken(), session);
        try{
            chessService.joinGame(command.getAuthToken(), new Request.JoinGame(command.getPlayerColor(), command.getGameID()));
            // send chess board
            ChessBoard game = chessService.getBoard(command.getAuthToken(), command.getGameID());
            LoadGameMessage message = new LoadGameMessage(LOAD_GAME, game, command.getPlayerColor().toLowerCase().equals("white"));
            session.getRemote().sendString(new Gson().toJson(message));
            
        }
        catch(ResponseException ex){
            ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException{
        System.out.println("Move");
        // TODO: implement
    }

    private void leave(UserGameCommand command, Session session) throws IOException{
        connections.remove(command.getAuthToken());
        ServerMessage message = new ServerMessage(ServerMessageType.SUCCESS);
        session.getRemote().sendString(new Gson().toJson(message));

    }

    private void resign(UserGameCommand command, Session session) throws IOException{
        connections.remove(command.getAuthToken());
        try {
            chessService.deleteGame(command.getAuthToken(), new Request.DeleteGame(command.getGameID()));
            ServerMessage message = new ServerMessage(ServerMessageType.SUCCESS);
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (ResponseException ex) {
            ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    

}
