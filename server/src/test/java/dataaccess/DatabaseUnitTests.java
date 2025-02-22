package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseUnitTests {

    private static MySqlAuthDAO authDAO;
    private static MySqlGameDAO gameDAO;
    private static MySqlUserDAO userDAO;

    private String authToken;

    @BeforeAll
    public static void init(){
        try{
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
            authDAO = new MySqlAuthDAO();
        }
        catch(ResponseException ex){
            System.out.println(ex.toString());
        }
    }

    // Restart 
    @BeforeEach
    public void setup(){
        try{
            // Clear databases
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();

            // Add example user
            UserData user = new UserData("real user", "password", "example@email.com");
            userDAO.addUserData(user);
            authToken = generateToken();
            authDAO.addAuthData(new AuthData(authToken, "real user"));

            // set up game
            gameDAO.createGame("real game");

        }
        catch(ResponseException ex){
            System.out.println(ex.toString());
        }
    }
    
    // User DAO methods
    
    // positive addUserData
    @Test
    @Order(1)
    @DisplayName("Valid user registration")
    public void validRegister() throws ResponseException{
        UserData user = new UserData("username", "password", "example@email.com");

        userDAO.addUserData(user);
    }

    // negative addUserData
    @Test
    @Order(2)
    @DisplayName("Invalid user registration")
    public void invalidRegister() throws ResponseException{
        UserData user1 = new UserData("username", "password", "example@email.com");
        UserData user2 = new UserData("username", "password", "example@email.com");

        userDAO.addUserData(user1);
        Assertions.assertThrows(ResponseException.class, () -> userDAO.addUserData(user2));
    }

    // positive getUser
    @Test
    @Order(3)
    @DisplayName("valid user retrevial")
    public void validGetUser() throws ResponseException{
        UserData user1 = new UserData("username", "password", "example@email.com");
        userDAO.addUserData(user1);

        userDAO.getUser("username");
    }

    // negative getUser
    @Test
    @Order(4)
    @DisplayName("retrieve user that doesn't exist")
    public void invalidGetUser() throws ResponseException{
        UserData user1 = new UserData("username", "password", "example@email.com");
        userDAO.addUserData(user1);

        Assertions.assertThrows(ResponseException.class, () -> userDAO.getUser("username1"));
    }

    // Positive clear
    @Test
    @Order(5)
    @DisplayName("Positive clear")
    public void validClear() throws ResponseException{
        UserData user1 = new UserData("username", "password", "example@email.com");
        userDAO.addUserData(user1);
        userDAO.clear();

        Assertions.assertThrows(ResponseException.class, () -> userDAO.getUser("username"));
    }

    // Auth DAO methods

    // positive addAuthData
    @Test
    @Order(6)
    public void validAddAuth() throws ResponseException{
        String token = generateToken();
        AuthData auth = new AuthData(token, "real user");
        authDAO.addAuthData(auth);
    }

    // negative addAuthData
    @Test
    @Order(6)
    public void invalidAddAuth() throws ResponseException{
        AuthData auth = new AuthData(authToken, "fake user");
        Assertions.assertThrows(ResponseException.class, () -> authDAO.addAuthData(auth));
    }

    // positive getAuth
    @Test
    @Order(7)
    public void validGetAuth() throws ResponseException{
        authDAO.getAuth(authToken);
    }

    // negative getAuth
    @Test
    @Order(8)
    public void invalidGetAuth() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> authDAO.getAuth(generateToken()));
    }

    // positive removeAuthData
    @Test
    @Order(9)
    public void validRemoveAuth() throws ResponseException{
        authDAO.removeAuthData(new AuthData(authToken, "real user"));
    }

    // negative removeAuthData
    @Test
    @Order(9)
    public void invalidRemoveAuth() throws ResponseException, SQLException{
        var conn = DatabaseManager.getConnection();
        var statement = "DROP TABLE auth";
        var drop = conn.prepareStatement(statement);
        drop.executeUpdate();
        Assertions.assertThrows(ResponseException.class, () -> authDAO.removeAuthData(new AuthData(authToken, "fake user")));
    }

    // positive clear
    @Test
    @Order(10)
    public void validAuthClear() throws ResponseException{
        authDAO.clear();

        Assertions.assertThrows(ResponseException.class, () -> authDAO.getAuth(authToken));
    }

    // Game DAO methods

    // positive createGame
    @Test
    @Order(11)
    public void validCreateGame() throws ResponseException{
        gameDAO.createGame("game");
    }

    // negative createGame
    @Test
    @Order(12)
    public void invalidCreateGame() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.createGame(""));
    }

    // positive getGame
    @Test
    @Order(13)
    public void validGetGame() throws ResponseException{
        gameDAO.getGame(1);
    }

    // negative getGame
    @Test
    @Order(14)
    public void invalidGetGame() throws ResponseException, SQLException{
        var conn = DatabaseManager.getConnection();
        var statement = "DROP TABLE game";
        var drop = conn.prepareStatement(statement);
        drop.executeUpdate();
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.getGame(-1));
    }

    // positive listGames
    @Test
    @Order(15)
    public void validListGames() throws ResponseException{
        ArrayList<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.size() == 1);
    }

    // negative listGames
    @Test
    @Order(16)
    public void invalidListGames() throws ResponseException, SQLException{
        var conn = DatabaseManager.getConnection();
        var statement = "DROP TABLE game";
        var drop = conn.prepareStatement(statement);
        drop.executeUpdate();
        Assertions.assertThrows(ResponseException.class, () -> gameDAO.listGames());
    }

    // positive updateGame
    @Test
    @Order(17)
    public void validUpdateGame() throws ResponseException{
        gameDAO.updateGame(1, null, "That would be me");
    }

    // negative updateGame
    @Test
    @Order(18)
    public void invalidUpdateGame() throws ResponseException, SQLException{
        var conn = DatabaseManager.getConnection();
        var statement = "DROP TABLE game";
        var drop = conn.prepareStatement(statement);
        drop.executeUpdate();
        Assertions.assertThrows(ResponseException.class, () ->gameDAO.updateGame(1, null, "That would be me"));
    }

    // positive clear
    @Test
    @Order(19)
    public void gameClear() throws ResponseException{
        gameDAO.clear();
    }


    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
