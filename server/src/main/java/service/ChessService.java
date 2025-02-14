package service;
import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

    public Result.Delete clear(Request.Delete clearDatabaseRequest){
        // clear authDAO
        authAccess.clear();
        // clear userDAO
        gameAccess.clear();
        // clear gameDAO
        userAccess.clear();

        return new Result.Delete("");
    }

    public Result.GetGames showGames(Request.GetGames showGameRequest) {
        if(checkAuth(showGameRequest.authToken())){
            return new Result.GetGames(gameAccess.listGames(), "");
        }
        return new Result.GetGames(null, "Error: unauthorized");
    }

    public Result.CreateGame createGame(String authToken, Request.CreateGame createGameRequest){
        if(checkAuth(authToken)){
            return new Result.CreateGame(gameAccess.createGame(createGameRequest.gameName()));
        }
        return new Result.CreateGame(-1);
    }

    public Result.JoinGame joinGame(String authToken, Request.JoinGame joinGameRequest) {
        if(joinGameRequest.playerColor() == null || joinGameRequest.playerColor().isEmpty() || joinGameRequest.gameID() == 0){
            return new Result.JoinGame("Error: bad request");
        }
        if(checkAuth(authToken)){
            switch (joinGameRequest.playerColor()) {
                case "WHITE" -> {
                    if(gameAccess.getGame(joinGameRequest.gameID()) != null && gameAccess.getGame(joinGameRequest.gameID()).whiteUsername() == null){
                        gameAccess.updateGame(joinGameRequest.gameID(), authAccess.getAuth(authToken).username(), null);
                        return new Result.JoinGame("");
                    }
                    else{
                        return new Result.JoinGame("Error: already taken");
                    }
                }
                case "BLACK" -> {
                    if(gameAccess.getGame(joinGameRequest.gameID()) != null && gameAccess.getGame(joinGameRequest.gameID()).blackUsername() == null){
                        gameAccess.updateGame(joinGameRequest.gameID(), null, authAccess.getAuth(authToken).username());
                        return new Result.JoinGame("");
                    }
                    else{
                        return new Result.JoinGame("Error: already taken");
                    }
                }
                default -> {
                    return new Result.JoinGame("Error: bad request");
                }
            }
        }
        return new Result.JoinGame("Error: unauthorized");
    }

    public Result.Register register(Request.Register registerRequest) {
        if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            return new Result.Register(null, null, "Error: bad request");
        }
        String username = registerRequest.username();
        UserData user = new UserData(username, registerRequest.password(), registerRequest.email());
        try{
            userAccess.addUserData(user);
        }
        catch(DataAccessException ex){
            return new Result.Register(null, null, "Error: already taken");
        }
        String token;
        do{
            token = generateToken();
        }
        while(authAccess.getAuth(token) != null);
        authAccess.addAuthData(new AuthData(token, username));
        return new Result.Register(username, token, "");
    }

    public Result.Login login(Request.Login loginRequest){
        UserData user = userAccess.getUser(loginRequest.username());
        if(user != null){
            String password = loginRequest.password();
            if(user.password().equals(password)){
                String token = generateToken();
                authAccess.addAuthData(new AuthData(token, user.username()));
                return new Result.Login(user.username(), token);
            }
        }
        return new Result.Login(null, null);
    }

    public Result.Logout logout(Request.Logout logoutRequest){
        AuthData authData = authAccess.getAuth(logoutRequest.authToken());
        if(authData != null){
            authAccess.removeAuthData(authData); 
            return new Result.Logout("");
        }
        return new Result.Logout("Error: unauthorized");
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }

    private boolean checkAuth(String authToken){
        return (authAccess.getAuth(authToken) != null);
    }


}
