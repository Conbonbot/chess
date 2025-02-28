package websocket.messages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage{
    
    private ChessBoard board;
    private boolean whitePerspective;

    public LoadGameMessage(ServerMessageType type, ChessBoard board, boolean whitePerspective){
        super(type);
        this.board = board;
        this.whitePerspective = whitePerspective;
    }

    public ChessBoard getBoard(){
        return board;
    }

    public boolean isWhite(){
        return whitePerspective;
    }
}
