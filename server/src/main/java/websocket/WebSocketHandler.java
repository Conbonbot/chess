package websocket;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exception.ResponseException;
import model.GameData;
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
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage.ServerMessageType;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.HIGHLIGHT;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

@WebSocket
public class WebSocketHandler{

    private final ChessService chessService;

    public WebSocketHandler(ChessService chessService){
        this.chessService = chessService;
    }

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
            case OBSERVE -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
            case HIGHLIGHT -> highlightBoard(new Gson().fromJson(message, HighlightCommand.class), session);
            default -> throw new IOException("Invalid");
        }
    }

    private void connect(ConnectCommand command, Session session) throws IOException, ResponseException{
        connections.add(command.getAuthToken(), command.getGameID(), session);
        try{
            // join as observer
            String str = chessService.getUsername(command.getAuthToken());
            if(command.getPlayerColor() != null){
                chessService.joinGame(command.getAuthToken(), new Request.JoinGame(command.getPlayerColor(), command.getGameID()));
                str += " has joined the game as color '" + command.getPlayerColor() + "'";
            }
            else{
                str += " has joined as an observer";
            }
            // send chess board
            ChessBoard game = chessService.getData(command.getAuthToken(), command.getGameID()).game().getBoard();
            LoadGameMessage message;
            if(command.getPlayerColor() != null){
                message = new LoadGameMessage(LOAD_GAME, game, command.getPlayerColor().toLowerCase().equals("white"));
            }
            else{
                message = new LoadGameMessage(LOAD_GAME, game, true);
            }
            session.getRemote().sendString(new Gson().toJson(message));
            // send messages to others
            NotificationMessage broadcastMessage = new NotificationMessage(NOTIFICATION, str);
            connections.broadcast(command.getAuthToken(), command.getGameID(), broadcastMessage);
        }
        catch(ResponseException ex){
            ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    

    private void sendBoard(RequestBoard command, Session session) throws IOException, ResponseException{
        ChessBoard game = chessService.getData(command.getAuthToken(), command.getGameID()).game().getBoard();
        LoadGameMessage message;
        if(command.getCommandType() == UserGameCommand.CommandType.OBSERVE){
            message = new LoadGameMessage(LOAD_GAME, game, true);
        }
        else{
            message = new LoadGameMessage(LOAD_GAME, game, command.isWhite());
        }
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void highlightBoard(HighlightCommand command, Session session) throws IOException, ResponseException{
        ChessGame game = chessService.getData(command.getAuthToken(), command.getGameID()).game();
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

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, ResponseException{
        GameData data;
        try{
            data = chessService.getData(command.getAuthToken(), command.getGameID());
        }
        catch(ResponseException ex){
            ErrorMessage message = new ErrorMessage(ERROR, "Bad authentication");
            session.getRemote().sendString(new Gson().toJson(message));
            return;
        }
        ChessGame game = data.game();
        String username = chessService.getUsername(command.getAuthToken());
        ChessBoard board = game.getBoard();
        ChessPiece startPiece = board.getPiece(command.getMove().getStartPosition());
        ErrorMessage message;
        // check if player is part of game
        if(data.blackUsername().equals(username) || data.whiteUsername().equals(username)){
            if(startPiece != null){
                try {
                    if(startPiece.getTeamColor() == TeamColor.WHITE && data.blackUsername().equals(username)
                    || startPiece.getTeamColor() == TeamColor.BLACK && data.whiteUsername().equals(username)){
                        throw new InvalidMoveException("This piece is not yours");
                    }
                    game.makeMove(command.getMove());
                    chessService.updateGame(command.getAuthToken(), command.getGameID(), game);
                    LoadGameMessage newBoard = new LoadGameMessage(LOAD_GAME, board, command.isWhite());
                    session.getRemote().sendString(new Gson().toJson(newBoard));
                    // broadcast new board & notification of made move
                    connections.broadcast(command.getAuthToken(), command.getGameID(), newBoard);
                    NotificationMessage notify = new NotificationMessage(NOTIFICATION, "Move made: " + command.getMove().toString());
                    connections.broadcast(command.getAuthToken(), command.getGameID(), notify);
                    return;
                } catch (InvalidMoveException e) {
                    // send error
                    message = new ErrorMessage(ERROR, e.getMessage());
                    session.getRemote().sendString(new Gson().toJson(message));
                    return;
                }
            }
            message = new ErrorMessage(ERROR, "This piece does not exist.");
            session.getRemote().sendString(new Gson().toJson(message));
        }
        else{
            message = new ErrorMessage(ERROR, "You cannot make a move as an observer.");
            session.getRemote().sendString(new Gson().toJson(message));
        }

    }

    private void leave(UserGameCommand command, Session session) throws IOException, ResponseException{
        // Find if exist as observer or in game
        GameData gameData = chessService.getData(command.getAuthToken(), command.getGameID());
        String username = chessService.getUsername(command.getAuthToken());
        String notification = username;
        if(gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)){
            chessService.removeGameUser(command.getAuthToken(), command.getGameID(), "white");
            notification += " has left the game (was color white)";
        } 
        else if(gameData.blackUsername() != null && gameData.blackUsername().equals(username)){
            chessService.removeGameUser(command.getAuthToken(), command.getGameID(), "black");
            notification += " has left the game (was color black)";
        }
        else{
            notification += " has left the game (was an observer)";
        }
        //
        connections.remove(command.getAuthToken());
        // Broadcast
        NotificationMessage notify = new NotificationMessage(NOTIFICATION, notification);
        connections.broadcast(command.getAuthToken(), command.getGameID(), notify);

    }

    private void resign(UserGameCommand command, Session session) throws IOException{
        connections.remove(command.getAuthToken());
        try {
            GameData game = chessService.getData(command.getAuthToken(), command.getGameID());
            String username = chessService.getUsername(command.getAuthToken());
            if(game.whiteUsername() != null && game.whiteUsername().equals(username)
            || game.blackUsername() != null && game.blackUsername().equals(username)){
                chessService.deleteGame(command.getAuthToken(), new Request.DeleteGame(command.getGameID()));
                NotificationMessage message = new NotificationMessage(NOTIFICATION, "You have resigned.");
                session.getRemote().sendString(new Gson().toJson(message));
                // Broadcast
                NotificationMessage notify = new NotificationMessage(NOTIFICATION, username + " has resigned.");
                connections.broadcast(command.getAuthToken(), command.getGameID(), notify);
            }
            else{
                ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, "You cannot resign a game you are not in.");
                session.getRemote().sendString(new Gson().toJson(message));
            }
            
        } catch (ResponseException ex) {
            ErrorMessage message = new ErrorMessage(ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    

}
