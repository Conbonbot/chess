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


    
}
