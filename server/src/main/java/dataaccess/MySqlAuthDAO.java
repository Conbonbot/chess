package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import exception.ResponseException;
import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws ResponseException{
        configureAuthDatabase();
    }

    @Override
    public AuthData addAuthData(AuthData authData) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "INSERT INTO auth (authToken, username) VALUES ('";
        statement += authData.authToken() + "','" + authData.username() + "');";
        try(var insert = conn.prepareStatement(statement)){
            insert.executeUpdate();
            return authData;
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "SELECT * FROM auth WHERE authToken = '" + authToken + "';";
        try(var query = conn.prepareStatement(statement)){
            ResultSet rs = query.executeQuery();
            while(rs.next()){
                if(authToken.equals(rs.getString("authToken"))){
                    return new AuthData(authToken, rs.getString("username"));
                }
            }
            throw new ResponseException(401, "Error: unauthorized");
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void removeAuthData(AuthData authData) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "DELETE FROM auth WHERE authToken = '" + authData.authToken() + "';";
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void clear() throws ResponseException{
        configureAuthDatabase();
        var conn = DatabaseManager.getConnection();
        var statement = "DELETE FROM auth";
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }


    private final String[] createAuthTable = {
        """
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` varchar(256) NOT NULL PRIMARY KEY,
          `username` varchar(256) NOT NULL
        )
        """
    };

    private void configureAuthDatabase() throws ResponseException{
        DatabaseManager.createDatabase();
        var conn = DatabaseManager.getConnection();
        for(var authStatement : createAuthTable){
            try(var authSanatized = conn.prepareStatement(authStatement)){
                authSanatized.executeUpdate();
            }
            catch(SQLException ex){
                throw new ResponseException(500, ex.toString());
            }
        }
    }

    @Override
    public boolean verifyUser(String hash, String providedClearTextPassword) {
        // read the previously hashed password from the database
        return BCrypt.checkpw(providedClearTextPassword, hash);
    }

   

}
