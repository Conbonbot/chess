package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;
    private final boolean isWhite;

	public MakeMoveCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, boolean isWhite, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.isWhite = isWhite;
    }

    public ChessMove getMove(){
        return move;
    }

    public boolean isWhite(){
        return isWhite;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((move == null) ? 0 : move.hashCode());
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
        MakeMoveCommand other = (MakeMoveCommand) obj;
        if (move == null) {
            if (other.move != null)
                return false;
        } else if (!move.equals(other.move))
            return false;
        return true;
    }

    
}
