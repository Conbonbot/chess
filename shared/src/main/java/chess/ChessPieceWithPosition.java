package chess;

/*
 * Represents a single chess piece.
 * Contains color, type, position, and possible moves
 */
public class ChessPieceWithPosition {

    private ChessPiece piece;
    private ChessPosition position;

    /*
     * Default constructor
     */
    public ChessPieceWithPosition(ChessPiece piece, ChessPosition position){
        this.piece = piece;
        this.position = position;
    }

    /*
     * Returns the piece itself
     */
    public ChessPiece getPiece(){
        return piece;
    }

    /*
     * Returns the position of the piece
     */
    public ChessPosition getPosition(){
        return position;
    }



}
