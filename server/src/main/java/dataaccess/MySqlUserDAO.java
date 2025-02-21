package dataaccess;

import java.sql.SQLException;

import model.UserData;


public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO(){
        configureDatabase();
    }

    @Override
    public UserData addUserData(UserData userData) throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addUserData'");
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

    private void configureDatabase(){
        try{
            DatabaseManager.createDatabase();
        }
        catch(DataAccessException ex){}
        try {
            var conn = DatabaseManager.getConnection();
            for(var statment : createTable){
                
                try(var sanatized = conn.prepareStatement(statment)){
                    sanatized.executeUpdate();
                }
                catch(SQLException ex){
                    System.out.println(ex.toString());
                }
            }
        } 
        catch (DataAccessException e) {
        }
    }

    
}
