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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isWhite ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!super.equals(obj)){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        RequestBoard other = (RequestBoard) obj;
        return isWhite == other.isWhite;
    }
    
}
