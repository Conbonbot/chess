package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private int nextID = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData game){
        game = new GameData(nextID++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        games.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID){
        for(Map.Entry<Integer, GameData> game : games.entrySet()){
            if(game.getValue().gameID() == gameID){
                return game.getValue();
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames(){
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(String gameName, int gameID){
        for(Map.Entry<Integer, GameData> game : games.entrySet()){
            if(game.getValue().gameID() == gameID){
                games.put(game.getKey(), new GameData(game.getValue().gameID(), 
                                                    game.getValue().blackUsername(), 
                                                    game.getValue().whiteUsername(), 
                                                    gameName, game.getValue().game()));
            }
        }
    }

    @Override
    public void clear(){
        System.out.println("Clear | GameDAO");
        games.clear();
    }
}  
