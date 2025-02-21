package results;
import java.util.List;

import model.GameData;

public record Result(){
    public record Register(String username, String authToken) {}
    public record Login(String username, String authToken) {}
    public record Logout(String errorMessage) {}
    public record GetGames(List<GameData> games) {}
    public record CreateGame(int gameID) {}
    public record JoinGame() {}
    public record Delete() {}
    public record Error(String message){}
}
