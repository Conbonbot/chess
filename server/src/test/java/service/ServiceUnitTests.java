package service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import requests.Request;


/**
 *
 * @author Connor
 */

 @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceUnitTests{

    private static ChessService service;

    private String authToken;

    @BeforeAll
    public static void init(){
        service = new ChessService(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO());
    }

    @BeforeEach
    public void setup(){
        service.clear(new Request.Delete());

        // Log in user
        Request.Register regReq = new Request.Register("username", "password", "example@email.com");
        var regRes = service.register(regReq);

        authToken = regRes.authToken();
    }
    
    // positive register
    @Test
    @DisplayName("Normal user registration")
    public void positiveRegister(){
        Request.Register regReq = new Request.Register("username1", "password1", "example1@email.com");

        var regRes = service.register(regReq);

        Assertions.assertTrue(regRes.errorMessage().isEmpty());
    }
    
    // negative register
    @Test
    @DisplayName("Invalid user registration")
    public void invalidRegister(){
        Request.Register regReq1 = new Request.Register("username1", "password1", "example1@email.com");
        Request.Register regReq2 = new Request.Register("username1", "password1", "example2@email.com");

        service.register(regReq1);
        var regRes2 = service.register(regReq2);

        Assertions.assertFalse(regRes2.errorMessage().isEmpty());
    }
    
    // positive login
    @Test
    @DisplayName("Normal user login")
    public void normalLogin(){
        Request.Login logReq = new Request.Login("username", "password");

        var logRes = service.login(logReq);

        Assertions.assertTrue(logRes.username().equals("username"));
    }

    // negative login
    @Test
    @DisplayName("Invalid user login")
    public void invalidLogin(){
        Request.Login logReq = new Request.Login("username", "password?");

        var logRes = service.login(logReq);

        Assertions.assertFalse(logRes.username() != null && logRes.authToken() != null);
    }

    // positive logout
    @Test
    @DisplayName("Valid user logout")
    public void validLogout(){
        Request.Logout logReq = new Request.Logout(authToken);

        var logRes = service.logout(logReq);

        Assertions.assertTrue(logRes.errorMessage().isEmpty());
    }

    // negative logout
    @Test
    @DisplayName("Invalid user logout")
    public void invalidLogout(){
        
        service.logout(logReq);
        var logRes = service.logout(logReq);

        Assertions.assertFalse(logRes.errorMessage().isEmpty());
    }

    // Positive showGames
    @Test
    @DisplayName("Valid show games")
    public void validShowGames(){
        Request.GetGames gamesReq = new Request.GetGames(authToken);
        
        var gamesRes = service.showGames(gamesReq);

        Assertions.assertTrue(gamesRes.errorMessage().isEmpty());
    }

    // Negative showGames
    @Test
    @DisplayName("Invalid show games")
    public void invalidShowGames(){
        Request.Logout logReq = new Request.Logout(authToken);
        Request.GetGames gamesReq = new Request.GetGames(authToken);
        service.logout(logReq);
        var gamesRes = service.showGames(gamesReq);

        Assertions.assertFalse(gamesRes.errorMessage().isEmpty());
    }

    // positive createGame
    @Test
    @DisplayName("Valid createGame")
    public void validCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");
        
        var gameRes = service.createGame(authToken, gameReq);

        Assertions.assertTrue(gameRes.gameID() != -1);
    }

    // negative createGame
    @Test
    @DisplayName("Invalid creation")
    public void invalidCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");
        Request.Logout logReq = new Request.Logout(authToken);
        service.logout(logReq);

        var gameRes = service.createGame(authToken, gameReq);

        Assertions.assertFalse(gameRes.gameID() == 1);
    }

    // positive joinGame
    // negative joinGame

    // Positive clear
    // Negative clear

    

    
}
