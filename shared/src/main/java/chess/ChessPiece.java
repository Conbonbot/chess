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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        String str = "";
        if(pieceColor == ChessGame.TeamColor.BLACK) {
            if(type == ChessPiece.PieceType.PAWN){
                str = "p";
            }
            else if(type == ChessPiece.PieceType.KNIGHT){
                str = "n";
            }
            else if(type == ChessPiece.PieceType.BISHOP){
                str = "b";
            }
            else if(type == ChessPiece.PieceType.ROOK){
                str = "r";
            }
            else if(type == ChessPiece.PieceType.QUEEN){
                str = "q";
            }
            else if(type == ChessPiece.PieceType.KING){
                str = "k";
            }
        }
        else{
            if(type == ChessPiece.PieceType.PAWN){
                str = "P";
            }
            else if(type == ChessPiece.PieceType.KNIGHT){
                str = "N";
            }
            else if(type == ChessPiece.PieceType.BISHOP){
                str = "B";
            }
            else if(type == ChessPiece.PieceType.ROOK){
                str = "R";
            }
            else if(type == ChessPiece.PieceType.QUEEN){
                str = "Q";
            }
            else if(type == ChessPiece.PieceType.KING){
                str = "K";
            }

        }
        return str;
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
        ArrayList<ChessMove> moves = new ArrayList<>();
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessPiece.PieceType currentPieceType = currentPiece.getPieceType();
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        ChessPiece piece;
        if(currentPieceType == PieceType.KING) {
            // up
            if(i > 0){
                // left
                if(j > 0) {
                    piece = board.getPiecebyIndex(i - 1, j - 1);
                    if (piece == null || piece.getTeamColor() != currentTeamColor) {
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-1), arrayToCol(j-1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                // straight
                piece = board.getPiecebyIndex(i-1,j);
                if(piece == null || piece.getTeamColor() != currentTeamColor){
                    ChessPosition endPosition = new ChessPosition(arrayToRow(i-1), arrayToCol(j));
                    ChessMove move = new ChessMove(myPosition, endPosition);
                    moves.add(move);
                }
                // right
                if(j < 7) {
                    piece = board.getPiecebyIndex(i - 1, j + 1);
                    if (piece == null || piece.getTeamColor() != currentTeamColor) {
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-1), arrayToCol(j+1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
            // down
            if(i < 7){
                // left
                if(j > 0) {
                    piece = board.getPiecebyIndex(i + 1, j - 1);
                    if (piece == null || piece.getTeamColor() != currentTeamColor) {
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+1), arrayToCol(j-1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                // straight
                piece = board.getPiecebyIndex(i+1,j);
                if(piece == null || piece.getTeamColor() != currentTeamColor){
                    ChessPosition endPosition = new ChessPosition(arrayToRow(i+1), arrayToCol(j));
                    ChessMove move = new ChessMove(myPosition, endPosition);
                    moves.add(move);
                }
                // right
                if(j < 7) {
                    piece = board.getPiecebyIndex(i + 1, j + 1);
                    if (piece == null || piece.getTeamColor() != currentTeamColor) {
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+1), arrayToCol(j+1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
            // left
            if(j > 0){
                piece = board.getPiecebyIndex(i,j-1);
                if(piece == null || piece.getTeamColor() != currentTeamColor){
                    ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j-1));
                    ChessMove move = new ChessMove(myPosition, endPosition);
                    moves.add(move);
                }
            }
            // right
            if(j < 7){
                piece = board.getPiecebyIndex(i,j+1);
                if(piece == null || piece.getTeamColor() != currentTeamColor){
                    ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j+1));
                    ChessMove move = new ChessMove(myPosition, endPosition);
                    moves.add(move);
                }
            }
        }
        if(currentPieceType == PieceType.BISHOP || currentPieceType == PieceType.QUEEN) {
            // up left
            if(i > 0 && j > 0){
                int i_ = i-1;
                int j_ = j-1;
                do{
                    piece = board.getPiecebyIndex(i_, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_--;
                        j_--;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ >= 0 && j_ >= 0);

            }
            // up right
            if(i > 0 && j < 7){
                int i_ = i-1;
                int j_ = j+1;
                do{
                    piece = board.getPiecebyIndex(i_, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_--;
                        j_++;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ >= 0 && j_ <= 7);
            }
            // down left
            if(i < 7 && j > 0){
                int i_ = i+1;
                int j_ = j-1;
                do{
                    piece = board.getPiecebyIndex(i_, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_++;
                        j_--;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ <= 7 && j_ >= 0);

            }
            // down right
            if(i < 7 && j < 7){
                int i_ = i+1;
                int j_ = j+1;
                do{
                    piece = board.getPiecebyIndex(i_, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_++;
                        j_++;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ <= 7 && j_ <= 7);

            }
        }
        if(currentPieceType == PieceType.ROOK || currentPieceType == PieceType.QUEEN) {
            // up
            if(i > 0){
                int i_ = i-1;
                do{
                    piece = board.getPiecebyIndex(i_, j);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_--;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ >= 0);
            }
            // down
            if(i < 7){
                int i_ = i+1;
                do{
                    piece = board.getPiecebyIndex(i_, j);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        i_++;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i_), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(i_ < 8);
            }
            // left
            if(j > 0){
                int j_ = j-1;
                do{
                    piece = board.getPiecebyIndex(i, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        j_--;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(j_ >= 0);
            }
            // right
            if(j < 7){
                int j_ = j+1;
                do{
                    piece = board.getPiecebyIndex(i, j_);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        j_++;
                    }
                    else if(piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j_));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                        break;
                    }
                    else{
                        break;
                    }
                }
                while(j_ < 8);

            }
        }
        if(currentPieceType == PieceType.KNIGHT ) {
            // up 2
            if(i > 1){
                //  left 1
                if(j > 0){
                    piece = board.getPiecebyIndex(i-2,j-1);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-2), arrayToCol(j-1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                //  right 1
                if(j < 7){
                    piece = board.getPiecebyIndex(i-2,j+1);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-2), arrayToCol(j+1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
            // down 2
            if(i < 6) {
                //  left 1
                if(j > 0){
                    piece = board.getPiecebyIndex(i+2,j-1);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+2), arrayToCol(j-1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                //  right 1
                if(j < 7){
                    piece = board.getPiecebyIndex(i+2,j+1);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+2), arrayToCol(j+1));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
            // left 2
            if(j > 1) {
                //  up 1
                if(i > 0){
                    piece = board.getPiecebyIndex(i-1,j-2);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-1), arrayToCol(j-2));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                //  down 1
                if(i < 7){
                    piece = board.getPiecebyIndex(i+1,j-2);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+1), arrayToCol(j-2));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
            // right 2
            if(j < 6) {
                //  up 1
                if(i > 0){
                    piece = board.getPiecebyIndex(i-1,j+2);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-1), arrayToCol(j+2));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                //  down 1
                if(i < 7){
                    piece = board.getPiecebyIndex(i+1,j+2);
                    if(piece == null || piece.getTeamColor() != currentTeamColor){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+1), arrayToCol(j+2));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
            }
        }
        if(currentPieceType == PieceType.PAWN) {
            ChessPiece piece2;
            if(currentTeamColor == ChessGame.TeamColor.BLACK) {
                // Check advance 2 (i = 2)
                if(i == 1){
                    piece = board.getPiecebyIndex(i+1,j);
                    piece2 = board.getPiecebyIndex(i+2,j);
                    if(piece == null && piece2 == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i+2), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                if(i < 8){
                    // regular
                    piece = board.getPiecebyIndex(i+1,j);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i + 1), arrayToCol(j));
                        if(i+1 == 7){
                            moves.addAll(promoMoves(myPosition, endPosition));
                        }
                        else {
                            ChessMove move = new ChessMove(myPosition, endPosition);
                            moves.add(move);
                        }
                    }
                    // take left
                    if(j > 0){
                        piece = board.getPiecebyIndex(i+1,j-1);
                        if(piece != null && piece.getTeamColor() != currentTeamColor){
                            ChessPosition endPosition = new ChessPosition(arrayToRow(i + 1), arrayToCol(j - 1));
                            if(i+1 == 7){
                                moves.addAll(promoMoves(myPosition, endPosition));
                            }
                            else {
                                ChessMove move = new ChessMove(myPosition, endPosition);
                                moves.add(move);
                            }
                        }
                    }
                    // take right
                    if(j < 7){
                        piece = board.getPiecebyIndex(i+1,j+1);
                        if(piece != null && piece.getTeamColor() != currentTeamColor){
                            ChessPosition endPosition = new ChessPosition(arrayToRow(i + 1), arrayToCol(j + 1));
                            if(i+1 == 7){
                                moves.addAll(promoMoves(myPosition, endPosition));
                            }
                            else {
                                ChessMove move = new ChessMove(myPosition, endPosition);
                                moves.add(move);
                            }
                        }
                    }
                }
            }
            else{
                // Check advance 2 (i = 6)
                if(i == 6){
                    piece = board.getPiecebyIndex(i-1,j);
                    piece2 = board.getPiecebyIndex(i-2,j);
                    if(piece == null && piece2 == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i-2), arrayToCol(j));
                        ChessMove move = new ChessMove(myPosition, endPosition);
                        moves.add(move);
                    }
                }
                if(i > 0) {
                    // regular advance
                    piece = board.getPiecebyIndex(i-1,j);
                    if(piece == null){
                        ChessPosition endPosition = new ChessPosition(arrayToRow(i - 1), arrayToCol(j));
                        if(i-1 == 0){
                            moves.addAll(promoMoves(myPosition, endPosition));
                        }
                        else {
                            ChessMove move = new ChessMove(myPosition, endPosition);
                            moves.add(move);
                        }
                    }
                    // take left
                    if(j > 0){
                        piece = board.getPiecebyIndex(i-1,j-1);
                        if(piece != null && piece.getTeamColor() != currentTeamColor){
                            ChessPosition endPosition = new ChessPosition(arrayToRow(i - 1), arrayToCol(j - 1));
                            if(i-1 == 0){
                                moves.addAll(promoMoves(myPosition, endPosition));
                            }
                            else {
                                ChessMove move = new ChessMove(myPosition, endPosition);
                                moves.add(move);
                            }
                        }
                    }
                    // take right
                    if(j < 7){
                        piece = board.getPiecebyIndex(i-1,j+1);
                        if(piece != null && piece.getTeamColor() != currentTeamColor){
                            ChessPosition endPosition = new ChessPosition(arrayToRow(i - 1), arrayToCol(j + 1));
                            if(i-1 == 0){
                                moves.addAll(promoMoves(myPosition, endPosition));
                            }
                            else {
                                ChessMove move = new ChessMove(myPosition, endPosition);
                                moves.add(move);
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }

    public ArrayList<ChessMove> promoMoves(ChessPosition startPosition, ChessPosition endPosition){
        //  Rook, Knight, Bishop, or Queen
        ArrayList<ChessMove> proMoves = new ArrayList<>();
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        return proMoves;
    }

    public int rowToArray(int row){
        return 8-row;
    }

    public int arrayToRow(int i){
        return 8-i;
    }

    public int colToArray(int col){
        return col-1;
    }

    public int arrayToCol(int j){
        return j+1;
    }


}
