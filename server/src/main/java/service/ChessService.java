package service;
import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
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

    public ChessService() throws ResponseException{
        authAccess = new MySqlAuthDAO();
        gameAccess = new MySqlGameDAO();
        userAccess = new MySqlUserDAO();        
    }

    public void clear(Request.Delete clearDatabaseRequest) throws ResponseException{
        if(authAccess == null || gameAccess == null || userAccess == null){
            throw new ResponseException(500, "Error: databases aren't initalized");
        }
        // clear authDAO
        authAccess.clear();
        // clear userDAO
        gameAccess.clear();
        // clear gameDAO
        userAccess.clear();
    }

    public Result.GetGames showGames(Request.GetGames showGameRequest) throws ResponseException{
        checkAuth(showGameRequest.authToken());
        return new Result.GetGames(gameAccess.listGames());
    }

    public Result.CreateGame createGame(String authToken, Request.CreateGame createGameRequest) throws ResponseException{
        checkAuth(authToken);
        return new Result.CreateGame(gameAccess.createGame(createGameRequest.gameName()));
    }

    public void joinGame(String authToken, Request.JoinGame joinGameRequest) throws ResponseException{
        if(joinGameRequest.playerColor() == null || joinGameRequest.playerColor().isEmpty() || joinGameRequest.gameID() == 0){
            throw new ResponseException(400, "Error: bad request");
        }
        checkAuth(authToken);
        GameData game = gameAccess.getGame(joinGameRequest.gameID());
        String username = authAccess.getAuth(authToken).username();
        switch (joinGameRequest.playerColor()) {
            case "WHITE" -> {
                if(game != null && game.whiteUsername() == null){
                    gameAccess.updateGame(joinGameRequest.gameID(), authAccess.getAuth(authToken).username(), null);
                }
                else{
                    throw new ResponseException(403, "Error: already taken");
                }
            }
            case "BLACK" -> {
                if(game != null && game.blackUsername() == null){
                    gameAccess.updateGame(joinGameRequest.gameID(), null, authAccess.getAuth(authToken).username());
                }
                else{
                    throw new ResponseException(403, "Error: already taken");
                }
            }
            default -> {
                throw new ResponseException(400, "Error: bad request");
            }
        }
    }

    public Result.Register register(Request.Register registerRequest) throws ResponseException{
        if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        String username = registerRequest.username();
        UserData user = new UserData(username, registerRequest.password(), registerRequest.email());
        userAccess.addUserData(user);
        String token = generateToken();
        authAccess.addAuthData(new AuthData(token, username));
        return new Result.Register(username, token);
    }

    public Result.Login login(Request.Login loginRequest) throws ResponseException{
        UserData user = userAccess.getUser(loginRequest.username());
        String password = loginRequest.password();
        if(authAccess.verifyUser(user.password(), password)){
            String token = generateToken();
            authAccess.addAuthData(new AuthData(token, user.username()));
            return new Result.Login(user.username(), token);
        }
        throw new ResponseException(401, "Error: unauthorized");

    }

    public void logout(Request.Logout logoutRequest) throws ResponseException{
        AuthData authData = authAccess.getAuth(logoutRequest.authToken());
        authAccess.removeAuthData(authData); 
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }

    private boolean checkAuth(String authToken) throws ResponseException{
        return (authAccess.getAuth(authToken) != null);
    }

    


}
