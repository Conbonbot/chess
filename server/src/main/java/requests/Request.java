package requests;

import chess.ChessGame;

public record Request() {
    public record Register(String username, String password, String email) {}
    public record Login(String username, String password) {}
    public record Logout(String authToken) {}
    public record GetGames(String authToken) {}
    public record CreateGame(String gameName) {}
    public record JoinGame(String playerColor, int gameID) {}
    public record Delete() {}
    public record Auth(String authToken) {}
    public record UpdateGame(int gameID, ChessGame game) {}
    public record DeleteGame(int gameID) {}
}
