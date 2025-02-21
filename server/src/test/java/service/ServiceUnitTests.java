package service;


import org.eclipse.jetty.client.api.Response;
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
import exception.ResponseException;
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
        try{
            var regRes = service.register(regReq);
            authToken = regRes.authToken();
        }
        catch(ResponseException ex){}

    }
    
    // positive register
    @Test
    @Order(1)
    @DisplayName("Normal user registration")
    public void positiveRegister(){
        Request.Register regReq = new Request.Register("username1", "password1", "example1@email.com");

        Assertions.assertDoesNotThrow(() -> service.register(regReq));
    }
    
    // negative register
    @Test
    @Order(2)
    @DisplayName("Invalid user registration")
    public void invalidRegister(){
        Request.Register regReq1 = new Request.Register("username1", "password1", "example1@email.com");
        Request.Register regReq2 = new Request.Register("username1", "password1", "example2@email.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            service.register(regReq1);
            service.register(regReq2);
        });
    }
    
    // positive login
    @Test
    @Order(3)
    @DisplayName("Normal user login")
    public void normalLogin(){
        Request.Login logReq = new Request.Login("username", "password");

        Assertions.assertDoesNotThrow(() -> service.login(logReq));
    }

    // negative login
    @Test
    @Order(4)
    @DisplayName("Invalid user login")
    public void invalidLogin(){
        Request.Login logReq = new Request.Login("username", "password?");

        Assertions.assertThrows(ResponseException.class, () -> service.login(logReq));
    }

    // positive logout
    @Test
    @Order(5)
    @DisplayName("Valid user logout")
    public void validLogout(){
        Request.Logout logReq = new Request.Logout(authToken);


        Assertions.assertDoesNotThrow(() -> service.logout(logReq));
    }

    // negative logout
    @Test
    @Order(6)
    @DisplayName("Invalid user logout")
    public void invalidLogout(){
        Request.Logout logReq = new Request.Logout(authToken);

        Assertions.assertThrows(ResponseException.class, () -> {
            service.logout(logReq);
            service.logout(logReq);
        });
    }

    // Positive showGames
    @Test
    @Order(7)
    @DisplayName("Valid show games")
    public void validShowGames(){
        Request.GetGames gamesReq = new Request.GetGames(authToken);

        Assertions.assertDoesNotThrow(() -> service.showGames(gamesReq));
    }

    // Negative showGames
    @Test
    @Order(8)
    @DisplayName("Invalid show games")
    public void invalidShowGames(){
        Request.Logout logReq = new Request.Logout(authToken);
        Request.GetGames gamesReq = new Request.GetGames(authToken);

        Assertions.assertThrows(ResponseException.class, () -> {
            service.logout(logReq);
            service.showGames(gamesReq);
        });
    }

    // positive createGame
    @Test
    @Order(9)
    @DisplayName("Valid createGame")
    public void validCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");

        Assertions.assertDoesNotThrow(() -> service.createGame(authToken, gameReq));
    }

    // negative createGame
    @Test
    @Order(10)
    @DisplayName("Invalid creation")
    public void invalidCreateGame(){
        Request.CreateGame gameReq = new Request.CreateGame("game");
        Request.Logout logReq = new Request.Logout(authToken);

        Assertions.assertThrows(ResponseException.class, () -> {
            service.logout(logReq);
            service.createGame(authToken, gameReq);
        });
    }

    // positive joinGame
    @Test
    @Order(11)
    @DisplayName("valid joinGame")
    public void validJoinGame(){
        Request.JoinGame joinReq = new Request.JoinGame("WHITE", 1);

        Assertions.assertDoesNotThrow(() -> {
            service.createGame(authToken, new Request.CreateGame("game_test"));
            service.joinGame(authToken, joinReq);
        });
    }

    // negative joinGame
    @Test
    @Order(12)
    @DisplayName("Invalid joinGame")
    public void invalidJoinGame(){
        Request.JoinGame joinReq = new Request.JoinGame("WHITE", 1);

        Assertions.assertThrows(ResponseException.class, () -> {
            service.createGame(authToken, new Request.CreateGame("game"));
            service.logout(new Request.Logout(authToken));

            service.joinGame(authToken, joinReq);
        });
    }

    // Positive clear
    @Test
    @Order(13)
    @DisplayName("Valid clear")
    public void validClear(){
        Request.Delete delReq = new Request.Delete();

        Assertions.assertDoesNotThrow(() -> service.clear(delReq));
    }

    // Negative clear
    @Test
    @Order(14)
    @DisplayName("Invalid clear")
    public void invalidClear(){
        Request.Delete delReq = new Request.Delete();
        ChessService invalidService = new ChessService(null, null, null);

        Assertions.assertThrows(ResponseException.class, () -> invalidService.clear(delReq));
    }

    

    
}
