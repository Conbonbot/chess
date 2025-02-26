package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import exception.ResponseException;
import server.Server;
import serverFacade.ServerFacade;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @AfterAll
    public static void stopServer() {
        facade.resetDatabase();
        server.stop();
    }

    @BeforeAll
    public static void init() throws ResponseException{
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }


    @BeforeEach
    public void setup() throws Exception{
        facade.resetDatabase();
        facade.register("register realUser coolPassword email");
        facade.logout();
    }

    public void login() throws Exception{
        facade.login("login realUser coolPassword");
    }
    
    @Test
    @Order(1)
    @DisplayName("Positive register")
    public void positiveRegister() throws Exception{
        Assertions.assertDoesNotThrow(() -> facade.register("register username password email"));
    }

    @Test
    @Order(2)
    @DisplayName("Register 2 users of same type")
    public void negativeRegister() throws Exception{
        facade.register("register username password email");
        Assertions.assertThrows(Exception.class, () -> facade.register("register username password email"));
    }

    @Test
    @Order(3)
    @DisplayName("Valid user login")
    public void positiveLogin() throws Exception{
        Assertions.assertDoesNotThrow(() -> facade.login("login realUser coolPassword"));
        
    }

    @Test
    @Order(4)
    @DisplayName("Valid username, invalid password")
    public void invalidPassword() throws Exception{
        Assertions.assertThrows(Exception.class, () -> facade.login("login realUser fakePassword"));
    }

    @Test
    @Order(5)
    @DisplayName("Invalid username, valid password")
    public void invalidUsername() throws Exception{
        Assertions.assertThrows(Exception.class, () -> facade.login("login fakeUser coolPassword"));
    }

    @Test
    @Order(6)
    @DisplayName("Invalid logout")
    public void invalidLogout() throws Exception{
        Assertions.assertThrows(Exception.class, () -> facade.logout());
    }

    @Test
    @Order(7)
    @DisplayName("Valid logout")
    public void validLogout() throws Exception{
        login();
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    @Order(8)
    @DisplayName("Valid create")
    public void validCreate() throws Exception{
        login();
        Assertions.assertDoesNotThrow(() -> facade.createGame("create coolGame"));
    }

    @Test
    @Order(9)
    @DisplayName("Invalid game creation")
    public void invalidCreate() throws Exception{
        login();
        Assertions.assertThrows(Exception.class, () -> facade.createGame("create really cool game"));
    }

    @Test
    @Order(10)
    @DisplayName("Valid join game")
    public void validJoin() throws Exception{
        login();
        facade.createGame("create game");
        Assertions.assertDoesNotThrow(() -> facade.joinGame("join 1 WHITE"));
    }

    @Test
    @Order(11)
    @DisplayName("join with word")
    public void joinWithWord() throws Exception{
        login();
        facade.createGame("create game");
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("join game WHITE"));
    }

    @Test
    @Order(12)
    @DisplayName("Join invalid game")
    public void invalidJoin() throws Exception{
        login();
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("join 1 BLACK"));
    }

    @Test
    @Order(13)
    @DisplayName("Join game as different colors")
    public void joinGameTwice() throws Exception{
        login();
        facade.createGame("create game");
        facade.joinGame("join 1 WHITE");
        Assertions.assertDoesNotThrow(() -> facade.joinGame("join 1 BLACK"));
    }

    @Test
    @Order(14)
    @DisplayName("Join game twice as same color")
    public void joinGameTwiceColor() throws Exception{
        login();
        facade.createGame("create game");
        facade.joinGame("join 1 WHITE");
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("join 1 WHITE"));
    }

    @Test
    @Order(15)
    @DisplayName("Observe game")
    public void validObserve() throws Exception{
        login();
        facade.createGame("create game");
        Assertions.assertDoesNotThrow(() -> facade.observeGame("observe 1"));
    }

    @Test
    @Order(16)
    @DisplayName("Observe invalid game")
    public void invalidObserve() throws Exception{
        login();
        facade.createGame("create game");
        Assertions.assertThrows(Exception.class, () -> facade.observeGame("observe 2"));
    }

    @Test
    @Order(17)
    @DisplayName("Observe game while logged out")
    public void loggedOutObserve() throws Exception{
        login();
        facade.createGame("create game");
        facade.logout();
        Assertions.assertThrows(Exception.class, () -> facade.observeGame("observe 2"));
    }

    @Test
    @Order(18)
    @DisplayName("Observe game using word")
    public void observeWithWord() throws Exception{
        login();
        facade.createGame("create game");
        Assertions.assertThrows(Exception.class, () -> facade.observeGame("observe game"));
    }

}
