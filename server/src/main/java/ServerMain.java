import chess.ChessGame;
import chess.ChessPiece;
import server.Server;


public class ServerMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        
        // For testing purposes, run on 8080
        Server server = new Server();
        server.run(3000);
        
        
    }
}