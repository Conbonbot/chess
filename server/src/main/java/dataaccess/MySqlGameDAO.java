package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws ResponseException{
        configureGameDatabase();
    }

    @Override
    public int createGame(String gameName) throws ResponseException{
        if(gameName.isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        var conn = DatabaseManager.getConnection();
        var statement = "INSERT INTO game (gameName, game) VALUES (?, ?);";
        try(var insert = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)){
            insert.setString(1, gameName);
            var json = new Gson().toJson(new ChessGame());
            insert.setString(2, json);
            insert.executeUpdate();
            ResultSet rs = insert.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            }
            throw new ResponseException(500, "Error: Issue with game creation");
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "SELECT * FROM game WHERE id = " + gameID;
        try(var query = conn.prepareStatement(statement)){
            ResultSet rs = query.executeQuery();
            if(rs.next()){
                return new GameData(rs.getInt(1), 
                    rs.getString("whiteUsername"), 
                    rs.getString("blackUsername"), 
                    rs.getString("gameName"),
                    new Gson().fromJson(rs.getString("game"), ChessGame.class));
            }
            return null;

        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "SELECT * FROM game";
        ArrayList<GameData> list = new ArrayList<>();
        try(var query = conn.prepareStatement(statement)){
            ResultSet rs = query.executeQuery();
            while(rs.next()){
                list.add(new GameData(rs.getInt(1), 
                    rs.getString("whiteUsername"), 
                    rs.getString("blackUsername"), 
                    rs.getString("gameName"),
                    new Gson().fromJson(rs.getString("game"), ChessGame.class)));
            }
            return list;
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername) throws ResponseException{
        GameData game = getGame(gameID);
        var conn = DatabaseManager.getConnection();
        var statement = "UPDATE game SET whiteUsername = '" + whiteUsername + "' WHERE id = " + game.gameID();
        if(blackUsername != null){
            statement = "UPDATE game SET blackUsername = '" + blackUsername + "' WHERE id = " + game.gameID();
        }
        try(var update = conn.prepareStatement(statement)){
            update.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame gameNew) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "UPDATE game SET game = '" + new Gson().toJson(gameNew) + "' WHERE id = " + gameID;
        try(var update = conn.prepareStatement(statement)){
            update.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void deleteGame(int gameID) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "DELETE FROM game WHERE id = " + gameID;
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }

    }

    @Override
    public void removeUser(int gameID, String playerColor) throws ResponseException{
        var conn = DatabaseManager.getConnection();
        var statement = "UPDATE game SET whiteUsername = null WHERE id = " + gameID;
        if(playerColor.equals("black")){
            statement = "UPDATE game SET blackUsername = null WHERE id = " + gameID;
        }
        try(var remove = conn.prepareStatement(statement)){
            remove.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, ex.toString());
        }
    }

    @Override
    public void clear() throws ResponseException{
        configureGameDatabase();
        var conn = DatabaseManager.getConnection();
        var statement = "DELETE FROM game";
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, "Error: bad database request");
        }
        // reset autoincrement
        statement = "ALTER TABLE game AUTO_INCREMENT 1";
        try(var delete = conn.prepareStatement(statement)){
            delete.executeUpdate();
        }
        catch(SQLException ex){
            throw new ResponseException(500, "Error: bad database request");
        }
    }

    private final String[] createGameTable = {
        """
        CREATE TABLE IF NOT EXISTS game (
          `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` longtext NOT NULL
        )
        """
    };

    private void configureGameDatabase() throws ResponseException{
        DatabaseManager.createDatabase();
        var conn = DatabaseManager.getConnection();
        for(var gameStatement : createGameTable){
            try(var gameSanatized = conn.prepareStatement(gameStatement)){
                gameSanatized.executeUpdate();
            }
            catch(SQLException ex){
                throw new ResponseException(500, ex.toString());
            }
        }
    }
    
}
