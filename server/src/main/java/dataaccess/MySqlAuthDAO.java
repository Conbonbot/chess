package dataaccess;

import java.sql.SQLException;

import exception.ResponseException;
import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{

    public MySqlAuthDAO() throws ResponseException{
        configureDatabase();
    }

    @Override
    public AuthData addAuthData(AuthData authData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAuthData'");
    }

    @Override
    public AuthData getAuth(String authToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public void removeAuthData(AuthData authData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAuthData'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    @Override
    public String generateAuth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateAuth'");
    }


    private final String[] createTable = {
        """
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL PRIMARY KEY
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
                // TODO: change response code
                throw new ResponseException(666, "Something happened");
            }
        }
    }

   

}
