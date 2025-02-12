package service;
import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.Request;
import results.Result;


public class ChessService {

    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final UserDAO userAccess;
    
    public ChessService(AuthDAO authAccess, GameDAO gameAccess, UserDAO userAccess){
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clear(Request.Delete clearDatabaseRequest){
        System.out.println("Clear | Service");
        // clear authDAO
        authAccess.clear();
        // clear userDAO
        gameAccess.clear();
        // clear gameDAO
        userAccess.clear();
    }

    public Result.GetGames showGames(Request.GetGames showGameRequest) {

        return new Result.GetGames(null);
    }

    public Result.CreateGame createGame(Request.CreateGame createGameRequest){

        return new Result.CreateGame(0);
    }

    public void joinGame(Request.JoinGame joinGameRequest) {

    }

    public Result.Register register(Request.Register registerRequest) {
        if(userAccess.getUser(registerRequest.username()) == null){
            String username = registerRequest.username();
            UserData user = new UserData(username, registerRequest.password(), registerRequest.email());
            userAccess.addUserData(user);
            String token = generateToken();
            authAccess.addAuthData(new AuthData(token, username));
            return new Result.Register(username, token);
        }
        // Probably throw an error here
        return new Result.Register(null, null);
    }

    public Result.Login login(Request.Login loginRequest){

        return new Result.Login(null, null);
    }

    public void logout(Request.Logout logoutRequest){

    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }


}
