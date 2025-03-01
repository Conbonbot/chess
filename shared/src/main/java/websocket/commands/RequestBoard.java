package websocket.commands;

public class RequestBoard extends UserGameCommand{

    private final boolean isWhite;

    public RequestBoard(UserGameCommand.CommandType commandType, String authToken, Integer gameID, boolean isWhite){
        super(commandType, authToken, gameID);
        this.isWhite = isWhite;
    }

    public boolean isWhite(){
        return isWhite;
    }
    
}
