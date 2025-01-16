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
                // up
                if(row < 8 && (board.getPiece(new ChessPosition(row+1,col)) == null || board.getPiece(new ChessPosition(row+1,col)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col), null));
                // up right
                if(row < 8 && col < 8 && (board.getPiece(new ChessPosition(row+1,col+1)) == null || board.getPiece(new ChessPosition(row+1,col+1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));
                // up left
                if(row < 8 && col > 1 && (board.getPiece(new ChessPosition(row+1,col-1)) == null || board.getPiece(new ChessPosition(row+1,col-1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col-1), null));
                // right
                if(col < 8 && (board.getPiece(new ChessPosition(row,col+1)) == null || board.getPiece(new ChessPosition(row,col+1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col+1), null));
                // left
                if(col > 1 && (board.getPiece(new ChessPosition(row,col-1)) == null || board.getPiece(new ChessPosition(row,col-1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row, col-1), null));
                // down
                if(row > 1 && (board.getPiece(new ChessPosition(row-1,col)) == null || board.getPiece(new ChessPosition(row-1,col)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col), null));
                // down right
                if(row > 1 && col < 8 && (board.getPiece(new ChessPosition(row-1,col+1)) == null || board.getPiece(new ChessPosition(row-1,col+1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col+1), null));
                // down left
                if(row > 1 && col > 1 && (board.getPiece(new ChessPosition(row-1,col-1)) == null || board.getPiece(new ChessPosition(row-1,col-1)).getTeamColor() != currentpieceColor))
                    moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col-1), null));
            case PieceType.QUEEN:
                // TODO: Queen Moves
            case PieceType.BISHOP:
                // Move in diagonal lines
                // 4 options: up left, up right, down left, down right
                // up left
                for(int i = row, j = col; i < 8 && j > 1; i++, j--){
                    if(board.getPiece(new ChessPosition(i, j)) == null || board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    else
                        break;
                }
                // up right
                for(int i = row, j = col; i < 8 && j < 8; i++, j++){
                    if(board.getPiece(new ChessPosition(i, j)) == null || board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    else
                        break;
                }
                // down left
                for(int i = row, j = col; i > 1 && j > 1; i--, j--){
                    if(board.getPiece(new ChessPosition(i, j)) == null || board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    else
                        break;
                }
                // down right
                for(int i = row, j = col; i > 1 && j < 8; i--, j++){
                    if(board.getPiece(new ChessPosition(i, j)) == null || board.getPiece(new ChessPosition(i, j)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, j), null));
                    else
                        break;
                }
            case PieceType.KNIGHT:
                // TODO: Knight moves
            case PieceType.ROOK:
                // Move in straight lines
                // Only 4 options: left, right, up down
                // left
                for(int i = col; i > 0; i--){
                    if(board.getPiece(new ChessPosition(row, i)) == null || board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                    else
                        break;
                }
                // right
                for(int i = col; i < 8; i++){
                    if(board.getPiece(new ChessPosition(row, i)) == null || board.getPiece(new ChessPosition(row, i)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, i), null));
                    else
                        break;
                }
                // up
                for(int i = row; i < 8; i++){
                    if(board.getPiece(new ChessPosition(i, col)) == null || board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col), null));
                    else
                        break;
                }
                // down
                for(int i = row; i > 0; i--){
                    if(board.getPiece(new ChessPosition(i, col)) == null || board.getPiece(new ChessPosition(i, col)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(i, col), null));
                    else
                        break;
                }
            case PieceType.PAWN:
                // Normally move 1 square forward, if it is the first time then 2.
                // Capture diagonally
                // Check color, can move 2, can move forward, can take
                if(currentpieceColor == ChessGame.TeamColor.WHITE){
                    // Move 2
                    if(row == 2 && board.getPiece(new ChessPosition(row+2, col)) == null)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row, col+2), null));
                    // Advance
                    if(board.getPiece(new ChessPosition(row+1, col)) == null)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col), null));
                    // Take left
                    if(board.getPiece(new ChessPosition(row+1, col-1)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col-1), null));
                    // Take right
                    if(board.getPiece(new ChessPosition(row+1, col+1)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+1, col+1), null));
                }
                else {
                    // Move 2
                    if(row == 7 && board.getPiece(new ChessPosition(row-2, col)) == null)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-2, col), null));
                    // Advance
                    if(board.getPiece(new ChessPosition(row-1, col)) == null)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col), null));
                    // Take left
                    if(board.getPiece(new ChessPosition(row-1, col-1)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col-1), null));
                    // Take right
                    if(board.getPiece(new ChessPosition(row-1, col+1)).getTeamColor() != currentpieceColor)
                        moves.add(new ChessMove(myPosition, new ChessPosition(row-1, col+1), null));
                }
        }
        return moves;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        ChessPiece check = (ChessPiece) obj;
        return this.pieceColor == check.getTeamColor() && this.type == check.getPieceType();
    }

    @Override
    public int hashCode(){
        return Objects.hash(pieceColor, type);
    }


}
