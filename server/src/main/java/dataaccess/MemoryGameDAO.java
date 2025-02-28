package dataaccess;

import java.util.ArrayList;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private int nextID = 1;
    private final ArrayList<GameData> games = new ArrayList<>();

    @Override
    public int createGame(String gameName){
        GameData game = new GameData(nextID++, null, null, gameName, new ChessGame());
        games.add(game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID){
        int find = findGameIndex(gameID);
        if(find != -1){
            return games.get(find);
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames(){
        return games;
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername){
        int find = findGameIndex(gameID);
        if(find != -1){
            GameData currentGame = games.get(find);
            games.add(new GameData(gameID, 
                    (whiteUsername != null) ? whiteUsername : currentGame.whiteUsername(), 
                    (blackUsername != null) ? blackUsername : currentGame.blackUsername(),
                    currentGame.gameName(),
                    currentGame.game()
            ));
            games.remove(find);
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame gameNew){
        String whiteUsername = null;
        String blackUsername = null;
        String gameName = null;
        int i = -1;
        for(i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                whiteUsername = games.get(i).whiteUsername();
                blackUsername = games.get(i).blackUsername();
                gameName = games.get(i).gameName();
                break;
            }
        }
        if(i != -1){
            GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, gameNew);
            games.remove(i);
            games.add(newGame);
        }
    }

    @Override
    public void deleteGame(int gameID){
        games.remove(findGameIndex(gameID));
    }

    @Override
    public void clear(){
        nextID = 1;
        games.clear();
    }

    private int findGameIndex(int gameID){
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                return i;
            }
        }
        return -1;
    }
}  
