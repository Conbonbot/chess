package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentpieceColor = currentPiece.getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        // King
        switch(currentPiece.getPieceType()){
            case PieceType.KING:
                // Can move 1 square in any direction
                // up left
                if(row < 8 && col > 1 && (board.getPiece(new ChessPosition(row+1,col-1)) == null || board.getPiece(new ChessPosition(row+1,col-1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col-1)));
                }
                // up
                if(row < 8 && (board.getPiece(new ChessPosition(row+1,col)) == null || board.getPiece(new ChessPosition(row+1,col)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col)));
                }
                // up right
                if(row < 8 && col < 8 && (board.getPiece(new ChessPosition(row+1,col+1)) == null || board.getPiece(new ChessPosition(row+1,col+1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+1)));
                }
                // left
                if(col > 1 && (board.getPiece(new ChessPosition(row,col-1)) == null || board.getPiece(new ChessPosition(row,col-1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col-1)));
                }
                // right
                if(col < 8 && (board.getPiece(new ChessPosition(row,col+1)) == null || board.getPiece(new ChessPosition(row,col+1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col+1)));
                }
                // down left
                if(row > 1 && col > 1 && (board.getPiece(new ChessPosition(row-1,col-1)) == null || board.getPiece(new ChessPosition(row-1,col-1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col-1)));
                }
                // down
                if(row > 1 && (board.getPiece(new ChessPosition(row-1,col)) == null || board.getPiece(new ChessPosition(row-1,col)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col)));
                }
                // down right
                if(row > 1 && col < 8 && (board.getPiece(new ChessPosition(row-1,col+1)) == null || board.getPiece(new ChessPosition(row-1,col+1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col+1)));
                }
                break;
            case PieceType.QUEEN:
                // Rook moves
                for(int i = col-1; i > 0; i--){
                    if(board.getPiece(new ChessPosition(row, i)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                    }
                    else if(board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // right
                for(int i = col+1; i <= 8; i++){
                    if(board.getPiece(new ChessPosition(row, i)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                    }
                    else if(board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // up
                for(int i = row+1; i <= 8; i++){
                    if(board.getPiece(new ChessPosition(i, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                    }
                    else if(board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // down
                for(int i = row-1; i > 0; i--){
                    if(board.getPiece(new ChessPosition(i, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                    }
                    else if(board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // Bishop Moves
                moves.addAll(diagonalMovement(board, myPosition, row, col, currentpieceColor));
                break;
            case PieceType.BISHOP:
                moves.addAll(diagonalMovement(board, myPosition, row, col, currentpieceColor));
                break;
            case PieceType.KNIGHT:
                // up 2, left 1
                if(row < 7 && col > 1 && (board.getPiece(new ChessPosition(row+2, col-1)) == null || board.getPiece(new ChessPosition(row+2, col-1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+2, col-1)));
                }
                // up 2, right 1
                if(row < 7 && col < 8 && (board.getPiece(new ChessPosition(row+2, col+1)) == null || board.getPiece(new ChessPosition(row+2, col+1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+2, col+1)));
                }
                // down 2, left 1
                if(row > 2 && col > 1 && (board.getPiece(new ChessPosition(row-2, col-1)) == null || board.getPiece(new ChessPosition(row-2, col-1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-2, col-1)));
                }
                // down 2, right 1
                if(row > 2 && col < 8 && (board.getPiece(new ChessPosition(row-2, col+1)) == null || board.getPiece(new ChessPosition(row-2, col+1)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-2, col+1)));
                }
                // left 2, up 1
                if(col > 2 && row < 8 && (board.getPiece(new ChessPosition(row+1, col-2)) == null || board.getPiece(new ChessPosition(row+1, col-2)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col-2)));
                }
                // left 2, down 1
                if(col > 2 && row > 1 && (board.getPiece(new ChessPosition(row-1, col-2)) == null || board.getPiece(new ChessPosition(row-1, col-2)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col-2)));
                }
                // right 2, up 1
                if(col < 7 && row < 8 && (board.getPiece(new ChessPosition(row+1, col+2)) == null || board.getPiece(new ChessPosition(row+1, col+2)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+2)));
                }
                // right 2, down 1
                if(col < 7 && row > 1 && (board.getPiece(new ChessPosition(row-1, col+2)) == null || board.getPiece(new ChessPosition(row-1, col+2)).getTeamColor() != currentpieceColor)){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col+2)));
                }
                break;
            case PieceType.ROOK:
                // Move in straight lines
                // Only 4 options: left, right, up down
                // left
                for(int i = col-1; i > 0; i--){
                    if(board.getPiece(new ChessPosition(row, i)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                    }
                    else if(board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // right
                for(int i = col+1; i <= 8; i++){
                    if(board.getPiece(new ChessPosition(row, i)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                    }
                    else if(board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // up
                for(int i = row+1; i <= 8; i++){
                    if(board.getPiece(new ChessPosition(i, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                    }
                    else if(board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                // down
                for(int i = row-1; i > 0; i--){
                    if(board.getPiece(new ChessPosition(i, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                    }
                    else if(board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor){
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col)));
                        break;
                    }
                    else{
                        break;
                    }
                }
                break;
            case PieceType.PAWN:
                // Normally move 1 square forward, if it is the first time then 2.
                // Capture diagonally
                // Check color, can move 2, can move forward, can take
                if(currentpieceColor == ChessGame.TeamColor.WHITE){
                    // Move 2
                    if(row == 2 && board.getPiece(new ChessPosition(row+1, col)) == null && board.getPiece(new ChessPosition(row+2, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+2, col), null));
                    }
                    // Advance
                    if(board.getPiece(new ChessPosition(row+1, col)) == null){
                        if(row == 7){
                            moves.addAll(pawnPromotion(myPosition, row+1, col));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col), null));
                        }
                    }
                    // Take left
                    if(board.getPiece(new ChessPosition(row+1, col-1)) != null && board.getPiece(new ChessPosition(row+1, col-1)).getTeamColor() != currentpieceColor){
                        if(row == 7){
                            moves.addAll(pawnPromotion(myPosition, row+1, col-1));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col-1), null));
                        }
                    }
                    // Take right
                    if(board.getPiece(new ChessPosition(row+1, col+1)) != null && board.getPiece(new ChessPosition(row+1, col+1)).getTeamColor() != currentpieceColor){
                        if(row == 7){
                            moves.addAll(pawnPromotion(myPosition, row+1, col+1));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));
                        }
                    }
                }
                else {
                    // Move 2
                    if(row == 7 && board.getPiece(new ChessPosition(row-1, col)) == null && board.getPiece(new ChessPosition(row-2, col)) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-2, col), null));
                    }
                    // Advance
                    if(board.getPiece(new ChessPosition(row-1, col)) == null){
                        if(row == 2){
                            moves.addAll(pawnPromotion(myPosition, row-1, col));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col), null));
                        }
                    }
                    // Take left
                    if(board.getPiece(new ChessPosition(row-1, col-1)) != null && board.getPiece(new ChessPosition(row-1, col-1)).getTeamColor() != currentpieceColor){
                        if(row == 2){
                            moves.addAll(pawnPromotion(myPosition, row-1, col-1));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col-1), null));
                        }
                    }
                    // Take right
                    if(board.getPiece(new ChessPosition(row-1, col+1)) != null && board.getPiece(new ChessPosition(row-1, col+1)).getTeamColor() != currentpieceColor){
                        if(row == 2){
                            moves.addAll(pawnPromotion(myPosition, row-1, col+1));
                        }
                        else{
                            moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col+1), null));
                        }
                    }
                }
                break;
        }
        return moves;
    }

    private Collection<ChessMove> pawnPromotion(ChessPosition myPosition, int endRow, int endCol){
        Collection<ChessMove> promoMoves = new ArrayList<>();
        promoMoves.add(new ChessMove(myPosition, new ChessPosition(endRow, endCol), PieceType.QUEEN));
        promoMoves.add(new ChessMove(myPosition, new ChessPosition(endRow, endCol), PieceType.ROOK));
        promoMoves.add(new ChessMove(myPosition, new ChessPosition(endRow, endCol), PieceType.BISHOP));
        promoMoves.add(new ChessMove(myPosition, new ChessPosition(endRow, endCol), PieceType.KNIGHT));
        return promoMoves;
    }

    private Collection<ChessMove> diagonalMovement(ChessBoard board, ChessPosition myPosition, int row, int col, ChessGame.TeamColor currentpieceColor){
        Collection<ChessMove> moves = new ArrayList<>();
        // Move in diagonal lines
        // 4 options: up left, up right, down left, down right
        // up left
        for(int i = row+1, j = col-1; i <= 8 && j >= 1; i++, j--){
            if(board.getPiece(new ChessPosition(i, j)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            }
            else if(board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                break;
            }
            else{
                break;
            }
        }
        // up right
        for(int i = row+1, j = col+1; i <= 8 && j <= 8; i++, j++){
            if(board.getPiece(new ChessPosition(i, j)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            }
            else if(board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                break;
            }
            else{
                break;
            }
        }
        // down left
        for(int i = row-1, j = col-1; i >= 1 && j >= 1; i--, j--){
            if(board.getPiece(new ChessPosition(i, j)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            }
            else if(board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                break;
            }
            else{
                break;
            }
        }
        // down right
        for(int i = row-1, j = col+1; i >= 1 && j <= 8; i--, j++){
            if(board.getPiece(new ChessPosition(i, j)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
            }
            else if(board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor){
                moves.add(new ChessMove(myPosition, new ChessPosition(i, j)));
                break;
            }
            else{
                break;
            }
        }
        return moves;
    }



    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        ChessPiece check = (ChessPiece) obj;
        return this.pieceColor == check.getTeamColor() && this.type == check.getPieceType();
    }

    @Override
    public int hashCode(){
        return Objects.hash(pieceColor, type);
    }


}
