package service;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requests.Request;

public class DatabaseService {

    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final UserDAO userAccess;
    
    public DatabaseService(AuthDAO authAccess, GameDAO gameAccess, UserDAO userAccess){
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clear(Request.Delete clearDatabaseRequest){
        // clear authDAO
        
        // clear userDAO
        // clear gameDAO
    }
}
