package chess;

import java.util.Objects;

/*
 * Represents a single chess piece.
 * Contains color, type, position, and possible moves
 */
public class ChessPieceWithPosition {

    private final ChessPiece piece;
    private final ChessPosition position;

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
        return this.piece;
    }

    public void setPosition(int row, int col){
        this.position.setRow(row);
        this.position.setCol(col);
    }

    /*
     * Returns the position of the piece
     */
    public ChessPosition getPosition(){
        return this.position;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        ChessPieceWithPosition check = (ChessPieceWithPosition) obj;
        return this.piece.equals(check.getPiece()) && this.position.equals(check.getPosition());
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.piece, this.position);
    }



}
