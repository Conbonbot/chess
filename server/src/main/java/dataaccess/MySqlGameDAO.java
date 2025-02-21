package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

import model.GameData;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO(){
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public GameData getGame(int gameID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public ArrayList<GameData> listGames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear'");
    }

    private final String[] createTable = {
        """
        CREATE TABLE IF NOT EXISTS game (
          `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256),
          `game` blob
        )
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
