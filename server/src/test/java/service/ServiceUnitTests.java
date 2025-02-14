package service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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

    @BeforeEach
    public void setup(){
        service = new ChessService(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO());

        // Log in user
        Request.Register regReq = new Request.Register("username", "password", "example@email.com");
        var regRes = service.register(regReq);

        authToken = regRes.authToken();
    }
    
    // positive register
    @Test
    @Order(1)
    @DisplayName("Normal user registration")
    public void positiveRegister(){
        Request.Register regReq = new Request.Register("username1", "password1", "example1@email.com");

        var regRes = service.register(regReq);

        Assertions.assertTrue(regRes.errorMessage().isEmpty());
    }
    
    // negative register
    @Test
    @Order(2)
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
    @Order(3)
    @DisplayName("Normal user login")
    public void normalLogin(){
        Request.Login logReq = new Request.Login("username", "password");

        var logRes = service.login(logReq);

        Assertions.assertTrue(logRes.username().equals("username"));
    }

    // negative login
    @Test
    @Order(4)
    @DisplayName("Invalid user login")
    public void invalidLogin(){
        Request.Login logReq = new Request.Login("username", "password?");

        var logRes = service.login(logReq);

        Assertions.assertFalse(logRes.username() != null && logRes.authToken() != null);
    }

    // positive logout
    @Test
    @Order(5)
    @DisplayName("Valid user logout")
    public void validLogout(){
        Request.Logout logReq = new Request.Logout(authToken);

        var logRes = service.logout(logReq);

        Assertions.assertTrue(logRes.errorMessage().isEmpty());
    }

    // negative logout
    @Test
    @Order(6)
    @DisplayName("Invalid user logout")
    public void invalidLogout(){
        Request.Logout logReq = new Request.Logout(authToken);
        service.logout(logReq);
        var logRes = service.logout(logReq);

        Assertions.assertFalse(logRes.errorMessage().isEmpty());
    }

    // Positive showGames
    @Test
    @Order(7)
    @DisplayName("Valid show games")
    public void validShowGames(){
        Request.GetGames gamesReq = new Request.GetGames(authToken);
        
        var gamesRes = service.showGames(gamesReq);

        Assertions.assertTrue(gamesRes.errorMessage().isEmpty());
    }

    // Negative showGames
    @Test
    @Order(8)
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
    @Order(9)
    @DisplayName("Valid createGame")
    public void validCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");
        
        var gameRes = service.createGame(authToken, gameReq);

        Assertions.assertTrue(gameRes.gameID() != -1);
    }

    // negative createGame
    @Test
    @Order(10)
    @DisplayName("Invalid creation")
    public void invalidCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");
        Request.Logout logReq = new Request.Logout(authToken);
        service.logout(logReq);

        var gameRes = service.createGame(authToken, gameReq);

        Assertions.assertFalse(gameRes.gameID() == 1);
    }

    // positive joinGame
    @Test
    @Order(11)
    @DisplayName("valid joinGame")
    public void validJoinGame(){
        service.createGame(authToken, new Request.CreateGame("game_test"));
        Request.JoinGame joinReq = new Request.JoinGame("WHITE", 1);

        var joinRes = service.joinGame(authToken, joinReq);

        Assertions.assertTrue(joinRes.errorMessage().isEmpty());
    }

    // negative joinGame
    @Test
    @Order(12)
    @DisplayName("Invalid joinGame")
    public void invalidJoinGame(){
        service.createGame(authToken, new Request.CreateGame("game"));
        service.logout(new Request.Logout(authToken));
        Request.JoinGame joinReq = new Request.JoinGame("WHITE", 1);

        var joinRes = service.joinGame(authToken, joinReq);

        Assertions.assertFalse(joinRes.errorMessage().isEmpty());
    }

    // Positive clear
    @Test
    @Order(13)
    @DisplayName("Valid clear")
    public void validClear(){
        Request.Delete delReq = new Request.Delete();

        var delRes = service.clear(delReq);

        Assertions.assertTrue(delRes.errorMessage().isEmpty());
    }

    // Negative clear
    @Test
    @Order(14)
    @DisplayName("Invalid clear")
    public void invalidClear(){
        Request.Delete delReq = new Request.Delete();

        var delRes = service.clear(delReq);

        Assertions.assertFalse(delRes == null);
    }

    

    
}
