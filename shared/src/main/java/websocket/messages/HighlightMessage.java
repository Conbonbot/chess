package websocket.messages;

import java.util.ArrayList;

import chess.ChessBoard;
import chess.ChessPosition;

public class HighlightMessage extends ServerMessage{
    private final ChessBoard board;
    private final boolean whitePerspective;
    private final ChessPosition pos;
    private final ArrayList<ChessPosition> endLocations;

    public HighlightMessage(ServerMessageType type, ChessBoard board, 
            boolean whitePerspective, ChessPosition pos, ArrayList<ChessPosition> endLocations){
        super(type);
        this.board = board;
        this.whitePerspective = whitePerspective;
        this.pos = pos;
        this.endLocations = endLocations;
    }

    public ChessBoard getBoard(){
        return board;
    }

    public boolean isWhite(){
        return whitePerspective;
    }

    public ChessPosition getPos(){
        return pos;
    }

    public ArrayList<ChessPosition> getLocations(){
        return endLocations;
    }

}
