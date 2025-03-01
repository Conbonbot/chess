package websocket.commands;

import chess.ChessPosition;

public class HighlightCommand extends UserGameCommand{
    private ChessPosition pos;
    boolean isWhite;

    public HighlightCommand(CommandType commandType, String authToken, Integer gameID, ChessPosition pos, boolean isWhite){
        super(commandType, authToken, gameID);
        this.pos = pos;
        this.isWhite = isWhite;
    }

    public ChessPosition getPos(){
        return pos;
    }

    public boolean isWhite(){
        return isWhite;
    }


    
}
