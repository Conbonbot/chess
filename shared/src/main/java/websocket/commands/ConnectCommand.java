package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    private final String playerColor;

    public ConnectCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, String playerColor){
        super(commandType, authToken, gameID);
        this.playerColor = playerColor;
    }

    public String getPlayerColor(){
        return playerColor;
    }

    public enum ConnectType{
        WHITE,
        BLACK,
        OBSERVER
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((playerColor == null) ? 0 : playerColor.hashCode());
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
        ConnectCommand other = (ConnectCommand) obj;
        return playerColor.equals(other.playerColor);
    }
}
