package websocket.messages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage{
    
    private ChessBoard game;
    private boolean whitePerspective;

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
