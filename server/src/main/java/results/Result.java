package results;
import java.util.List;

import model.GameData;

public record Result(){
    public record Register(String username, String authToken, String errorMessage) {}
    public record Login(String username, String authToken) {}
    public record Logout(String errorMessage) {}
    public record GetGames(List<GameData> games, String errorMessage) {}
    public record CreateGame(int gameID) {}
    public record JoinGame(String errorMessage) {}
    public record Delete() {}
    public record Error(String message){}
}
