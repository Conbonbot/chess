package dataaccess;

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

    private void configureDatabase(){
        try{
            DatabaseManager.createDatabase();
        }
        catch(DataAccessException e){
            
        }
    }

    
}
