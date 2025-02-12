package dataaccess;

import java.util.ArrayList;

import model.GameData;

public interface GameDAO {
    int createGame(GameData gameData);
    GameData getGame(int gameID);
    ArrayList<GameData> listGames();
    void updateGame(String gameName, int gameID);

}