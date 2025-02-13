package dataaccess;

import java.util.ArrayList;

import model.GameData;

public interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID);
    ArrayList<GameData> listGames();
    void updateGame(int gameID, String whiteUsername, String blackUsername);
    void clear();

}