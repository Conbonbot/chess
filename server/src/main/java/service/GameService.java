package service;
import dataaccess.GameDAO;
import requests.Request;
import results.Result;


public class GameService {

    private final GameDAO gameAccess;

    public GameService(GameDAO gameAccess){
        this.gameAccess = gameAccess;
    }

    public Result.GetGames showGames(Request.GetGames showGameRequest) {

        return new Result.GetGames(null);
    }

    public Result.CreateGame createGame(Request.CreateGame createGameRequest){

        return new Result.CreateGame(0);
    }

    public void joinGame(Request.JoinGame joinGameRequest) {

    }
}
