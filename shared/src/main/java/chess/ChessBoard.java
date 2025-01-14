package chess;

import java.util.ArrayList;

import javax.management.monitor.GaugeMonitor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ArrayList<ChessPieceWithPosition> gamePieces = new ArrayList<ChessPieceWithPosition>();

    /*
     * Constructor for the class
     * Uses setWhite and setBlack to assemble the board
     */
    public ChessBoard() {
        setWhite();
        setBlack();
    }

    /*
     * An array holding the relative locations of the special pieces
     */
    private ChessPiece.PieceType[] specialPiecesOrder = {
        ChessPiece.PieceType.ROOK,
        ChessPiece.PieceType.KNIGHT,
        ChessPiece.PieceType.BISHOP,
        ChessPiece.PieceType.QUEEN,
        ChessPiece.PieceType.KING,
        ChessPiece.PieceType.BISHOP,
        ChessPiece.PieceType.KNIGHT,
        ChessPiece.PieceType.ROOK
    };

    /*
     * Creates all white pieces, and adds them to game_pieces
     */
    public void setWhite(){
        int col = 1;
        // Set special pieces (row = 1)
        for(ChessPiece.PieceType specialPiece : specialPiecesOrder){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.WHITE, specialPiece), 
                new ChessPosition(1, col++)
            ));
        }
        // Set pawns (row = 2)
        col = 1;
        for(int i = 0; i < 8; i++){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), 
                new ChessPosition(2, col++)
            ));
        }
    }

    /*
     * Creates all black pieces, and adds them to game_pieces
     */
    public void setBlack(){
        int col = 1;
        // Set special pieces (row = 7)
        for(ChessPiece.PieceType specialPiece : specialPiecesOrder){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.BLACK, specialPiece), 
                new ChessPosition(7, col++)
            ));
        }
        // Set pawns (row = 8)
        col = 1;
        for(int i = 0; i < 8; i++){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), 
                new ChessPosition(8, col++)
            ));
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        gamePieces.add(new ChessPieceWithPosition(piece, position));
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        gamePieces.clear();
        setBlack();
        setWhite();
    }
}
