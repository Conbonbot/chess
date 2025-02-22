package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import exception.ResponseException;
import model.UserData;


public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() throws ResponseException{
        configureDatabase();
    }

    @Override
    public UserData addUserData(UserData userData) throws ResponseException {
        var conn = DatabaseManager.getConnection();
        var statement = "SELECT username FROM user WHERE username=" + "'" + userData.username() + "';";
        try(var sanatizedInput = conn.prepareStatement(statement)){
            ResultSet rs = sanatizedInput.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                if(userData.username().equals(username)){
                    throw new ResponseException(401, "Error: already taken");
                }
            }
            // Insert user
            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            var insert = "INSERT INTO user (username, password, email) VALUES ('";
            insert += userData.username() + "','" + hashedPassword + "','" + userData.email() + "');";
            try(var insertUser = conn.prepareStatement(insert)){
                insertUser.executeUpdate();
            }
            return userData;

        }
        catch(SQLException ex){
            throw new ResponseException(400, "Error: bad request");
        }
    }

    @Override
    public UserData getUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    private final String[] createTable = {
        """
        CREATE TABLE IF NOT EXISTS user (
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          PRIMARY KEY (`username`)
        );
        """
    };

    private void configureDatabase() throws ResponseException{
        DatabaseManager.createDatabase();
        var conn = DatabaseManager.getConnection();
        for(var statment : createTable){
            try(var sanatized = conn.prepareStatement(statment)){
                sanatized.executeUpdate();
            }
            catch(SQLException ex){
                // TODO: change response code
                throw new ResponseException(666, "Something happened");
            }
        }
    }

    
}
