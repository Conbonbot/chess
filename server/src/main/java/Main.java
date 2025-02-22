import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import exception.ResponseException;
import server.Server;
import service.ChessService;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        
        // For testing purposes, run on 8080
        Server server;
        try{
            server = new Server(new ChessService(new MySqlAuthDAO(), new MySqlGameDAO(), new MySqlUserDAO()));
            server.run(3000);
        }
        catch(ResponseException ex){
            server = new Server(new ChessService(new MemoryAuthDAO(), new MemoryGameDAO(), new MemoryUserDAO()));
            server.run(3000);
        }
        
        
    }
}