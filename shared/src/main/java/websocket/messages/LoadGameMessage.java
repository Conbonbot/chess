package websocket.messages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage{
    
    private final ChessBoard game;
    private final boolean whitePerspective;

    public LoadGameMessage(ServerMessageType type, ChessBoard game, boolean whitePerspective){
        super(type);
        this.game = game;
        this.whitePerspective = whitePerspective;
    }

    public ChessBoard getBoard(){
        return game;
    }

    public boolean isWhite(){
        return whitePerspective;
    }
}
