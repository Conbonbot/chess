import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import server.Server;
import service.DatabaseService;
import service.GameService;
import service.UserService;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        // For testing purposes, run on 8080
        // set DAOs
        AuthDAO authAccess = new MemoryAuthDAO();
        GameDAO gameAccess = new MemoryGameDAO();
        UserDAO userAccess = new MemoryUserDAO();
        // set Service
        DatabaseService dbService = new DatabaseService(authAccess, gameAccess, userAccess);
        GameService gameService = new GameService(gameAccess);
        UserService userService = new UserService(userAccess);
        // set Server
        Server server = new Server(dbService, gameService, userService);
        server.run(3000);
        
    }
}