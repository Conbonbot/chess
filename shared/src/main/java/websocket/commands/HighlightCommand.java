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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        result = prime * result + (isWhite ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        HighlightCommand other = (HighlightCommand) obj;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return isWhite == other.isWhite;
    }

    
}
