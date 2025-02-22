package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

import exception.ResponseException;
import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws ResponseException{
        configureDatabase();
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
        var conn = DatabaseManager.getConnection();
        var statement = "DELETE FROM auth";
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }


    private final String[] createTable = {
        """
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` varchar(256) NOT NULL PRIMARY KEY,
          `username` varchar(256) NOT NULL
        )
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
                throw new ResponseException(500, ex.toString());
            }
        }
    }

   

}
