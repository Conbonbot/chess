import chess.ChessGame;
import chess.ChessPiece;
import server.Server;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        Server server = new Server();
        System.out.println("♕ 240 Chess Server: " + piece);
        // For testing purposes, run on 8080
        server.run(3000);
    }
}