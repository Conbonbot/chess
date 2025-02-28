package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import requests.Request;
import service.ChessService;
import websocket.commands.ConnectCommand;
import websocket.commands.HighlightCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.RequestBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.HighlightMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.HIGHLIGHT;
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
            case REQUEST_BOARD -> sendBoard(new Gson().fromJson(message, RequestBoard.class), session);
            case HIGHLIGHT -> highlightBoard(new Gson().fromJson(message, HighlightCommand.class), session);
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
            ErrorMessage message = new ErrorMessage(ServerMessageType.CONNECT_ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    private void sendBoard(RequestBoard command, Session session) throws IOException, ResponseException{
        ChessBoard game = chessService.getBoard(command.getAuthToken(), command.getGameID());
        LoadGameMessage message = new LoadGameMessage(LOAD_GAME, game, command.isWhite());
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void highlightBoard(HighlightCommand command, Session session) throws IOException, ResponseException{
        ChessGame game = chessService.getGame(command.getAuthToken(), command.getGameID());
        ChessBoard board = game.getBoard();
        if(board.getPiece(command.getPos()) != null){
            if(board.getPiece(command.getPos()).getTeamColor() == (command.isWhite() ? TeamColor.WHITE : TeamColor.BLACK)){
                // send it
                ArrayList<ChessPosition> endLocations = new ArrayList<>();
                for(ChessMove move : game.validMoves(command.getPos())){
                    endLocations.add(move.getEndPosition());
                }  
                HighlightMessage message = new HighlightMessage(HIGHLIGHT, board, command.isWhite(), command.getPos(), endLocations);
                session.getRemote().sendString(new Gson().toJson(message));
                return;
            }
            ErrorMessage message = new ErrorMessage(ERROR, "That piece does not belong to you");
            session.getRemote().sendString(new Gson().toJson(message));
            return;
        }
        ErrorMessage message = new ErrorMessage(ERROR, "No piece found");
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException{
        System.out.println("Move");
        // TODO: implement
    }

    private void leave(UserGameCommand command, Session session) throws IOException{
        connections.remove(command.getAuthToken());
        ServerMessage message = new ServerMessage(ServerMessageType.LEAVE);
        session.getRemote().sendString(new Gson().toJson(message));

    }

    private void resign(UserGameCommand command, Session session) throws IOException{
        connections.remove(command.getAuthToken());
        try {
            chessService.deleteGame(command.getAuthToken(), new Request.DeleteGame(command.getGameID()));
            ServerMessage message = new ServerMessage(ServerMessageType.RESIGN);
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (ResponseException ex) {
            ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    

}
