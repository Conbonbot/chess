package dataaccess;

import java.util.ArrayList;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

public interface GameDAO {
    int createGame(String gameName) throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    ArrayList<GameData> listGames() throws ResponseException;
    void updateGame(int gameID, String whiteUsername, String blackUsername) throws ResponseException;
    void updateGame(int gameID, ChessGame game) throws ResponseException;
    void deleteGame(int gameID) throws ResponseException;
    void clear() throws ResponseException;
}