package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition, endPosition;
    private final ChessPiece.PieceType promotionPiece;


    /*
     * Default constructor
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /*
     * Overloaded constructor for non-pawn pieces (promotion is null)
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    
    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        ChessMove check = (ChessMove) obj;
        boolean check1 = this.startPosition.equals(check.getStartPosition());
        boolean check2 = this.endPosition.equals(check.getEndPosition());
        boolean check3;
        // Promotion -> one could be null, or both, or neither
        if(this.promotionPiece != null && check.getPromotionPiece() != null)
            check3 = this.promotionPiece.equals(check.getPromotionPiece());
        else if((this.promotionPiece == null && check.getPromotionPiece() != null) || (this.promotionPiece != null && check.getPromotionPiece() == null))
            check3 = false;
        else
            check3 = true;
        return check1 && check2 && check3;

    }

    @Override
    public int hashCode(){
        return Objects.hash(this.startPosition, this.endPosition, this.promotionPiece);
    }

    @Override
    public String toString(){
        return "Start: " + startPosition + " | End: " + endPosition + "\n";
    }

    
}
