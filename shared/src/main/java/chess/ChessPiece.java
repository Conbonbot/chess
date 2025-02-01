package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;



/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    
    // Predicates to help shorten code
    // UL -> Up and Left check
    // DR -> Down and Right check
    private final Predicate<Integer> UL = x -> (x > 0);
    private final Predicate<Integer> DR = x -> (x < 7);
    private final Predicate<Integer> nop = x -> (x != null);
    private final Predicate<Integer> UL_move = x -> (x >= 0);
    private final Predicate<Integer> DR_move = x -> (x < 8);

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
        if(type != null){
            str = switch (type) {
                case PAWN -> "p";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case ROOK -> "r";
                case QUEEN -> "q";
                case KING -> "k";
            };
        }
        if(pieceColor == ChessGame.TeamColor.BLACK) {
            return str.toUpperCase();
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
     * 
     * Various enums used for code refactoring
     */
    private enum directions{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
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
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessPiece.PieceType currentPieceType = currentPiece.getPieceType();
        if(currentPieceType == PieceType.KING) {
            moves.addAll(kingMovement(myPosition, board));
        }
        if(currentPieceType == PieceType.BISHOP || currentPieceType == PieceType.QUEEN) {
            moves.addAll(diagonalMovement(myPosition, board));
        }
        if(currentPieceType == PieceType.ROOK || currentPieceType == PieceType.QUEEN) {
            moves.addAll(verticalMovement(myPosition, board));
        }
        if(currentPieceType == PieceType.KNIGHT ) {
           moves.addAll(knightMovement(myPosition, board));
        }
        if(currentPieceType == PieceType.PAWN) {
            moves.addAll(pawnMovement(myPosition, board));
        }

        moves.removeAll(Collections.singleton(null));
        return moves;
    }

    private ArrayList<ChessMove> pawnMovement(ChessPosition myPosition, ChessBoard board){
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        ArrayList<ChessMove> nullList = new ArrayList<>();
        nullList.add(null);
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        Predicate<ChessGame.TeamColor> isBlack = x -> (x == ChessGame.TeamColor.BLACK);
        if(isBlack.test(currentTeamColor)){
            // Check move 2, regular advance, take left/right
            pawnMoves.add((i == 1) ? advancePawn(board, myPosition, i+1, i+2, j) : null);
            pawnMoves.addAll((DR.test(i)) ? addPawnMove(board, myPosition, i+1, j, false) : nullList);
            pawnMoves.addAll((DR.test(i) && UL.test(j)) ? addPawnMove(board, myPosition, i+1, j-1, true) : nullList);
            pawnMoves.addAll((DR.test(i) && DR.test(j)) ? addPawnMove(board, myPosition, i+1, j+1, true) : nullList);
        }
        else{
            // Check move 2, regular advance, take left/right
            pawnMoves.add((i == 6) ? advancePawn(board, myPosition, i-1, i-2, j) : null);
            pawnMoves.addAll((UL.test(i)) ? addPawnMove(board, myPosition, i-1, j, false) : nullList);
            pawnMoves.addAll((UL.test(i) && UL.test(j)) ? addPawnMove(board, myPosition, i-1, j-1, true) : nullList);
            pawnMoves.addAll((UL.test(i) && DR.test(j)) ? addPawnMove(board, myPosition, i-1, j+1, true) : nullList);
        }
        return pawnMoves;
    }

    private ArrayList<ChessMove> knightMovement(ChessPosition myPosition, ChessBoard board){
        ArrayList<ChessMove> knightMoves = new ArrayList<>();
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        Predicate<Integer> UL_2 = x -> (x > 1); 
        Predicate<Integer> DR_2 = x -> (x < 6);
         // up 2 (left/right 1)
        knightMoves.add((UL_2.test(i) && UL.test(j)) ? addMove(board, myPosition, i-2, j-1) : null);
        knightMoves.add((UL_2.test(i) && DR.test(j)) ? addMove(board, myPosition, i-2, j+1) : null);
        // down 2 (left/right 1)
        knightMoves.add((DR_2.test(i) && UL.test(j)) ? addMove(board, myPosition, i+2, j-1) : null);
        knightMoves.add((DR_2.test(i) && DR.test(j)) ? addMove(board, myPosition, i+2, j+1) : null);
        // left 2 (up/down 1)
        knightMoves.add((UL_2.test(j) && UL.test(i)) ? addMove(board, myPosition, i-1, j-2) : null);
        knightMoves.add((UL_2.test(j) && DR.test(i)) ? addMove(board, myPosition, i+1, j-2) : null);
        // right 2 (up/down 1)
        knightMoves.add((DR_2.test(j) && UL.test(i)) ? addMove(board, myPosition, i-1, j+2) : null);
        knightMoves.add((DR_2.test(j) && DR.test(i)) ? addMove(board, myPosition, i+1, j+2) : null);
        return knightMoves;
    }

    private ArrayList<ChessMove> diagonalMovement(ChessPosition myPosition, ChessBoard board){
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        ArrayList<ChessMove> diagonalMoves = new ArrayList<>();
        ArrayList<ChessMove> nullList = new ArrayList<>();
        nullList.add(null);
        // up left
        diagonalMoves.addAll((UL.test(i) && UL.test(j)) ? linearMovement(myPosition, board, i, j, directions.UP_LEFT, UL_move, UL_move) : nullList);
        // up right
        diagonalMoves.addAll((UL.test(i) && DR.test(j)) ? linearMovement(myPosition, board, i, j, directions.UP_RIGHT, UL_move, DR_move) : nullList);
        // down left
        diagonalMoves.addAll((DR.test(i) && UL.test(j)) ? linearMovement(myPosition, board, i, j, directions.DOWN_LEFT, DR_move, UL_move) : nullList);
        // down right
        diagonalMoves.addAll((DR.test(i) && DR.test(j)) ? linearMovement(myPosition, board, i, j, directions.DOWN_RIGHT, DR_move, DR_move) : nullList);
        return diagonalMoves;
    }

    private ArrayList<ChessMove> verticalMovement(ChessPosition myPosition, ChessBoard board){
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        ArrayList<ChessMove> vertMoves = new ArrayList<>();
        ArrayList<ChessMove> nullList = new ArrayList<>();
        nullList.add(null);
        // up
        vertMoves.addAll((UL.test(i)) ? linearMovement(myPosition, board, i, j, directions.UP, UL_move, nop) : nullList);
        // down
        vertMoves.addAll((DR.test(i)) ? linearMovement(myPosition, board, i, j, directions.DOWN, DR_move, nop) : nullList);
        // left
        vertMoves.addAll((UL.test(j)) ? linearMovement(myPosition, board, i, j, directions.LEFT, nop, UL_move) : nullList);
        // right
        vertMoves.addAll((DR.test(j)) ? linearMovement(myPosition, board, i, j, directions.RIGHT, nop, DR_move) : nullList);
        return vertMoves;
    }

    private ArrayList<ChessMove> linearMovement(ChessPosition myPosition, ChessBoard board, int i, int j, ChessPiece.directions dir, 
        Predicate<Integer> cond1, Predicate<Integer> cond2){

        ArrayList<ChessMove> linearMoves =  new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        int i_temp = switch(dir) {
            case UP -> i-1;
            case DOWN -> i+1;
            case DOWN_LEFT -> i+1;
            case DOWN_RIGHT -> i+1;
            case LEFT -> i;
            case RIGHT -> i;
            case UP_LEFT -> i-1;
            case UP_RIGHT -> i-1;
        };
        int j_temp = switch(dir){
            case UP -> j;
            case DOWN -> j;
            case DOWN_LEFT -> j-1;
            case DOWN_RIGHT -> j+1;
            case LEFT -> j-1;
            case RIGHT -> j+1;
            case UP_LEFT -> j-1;
            case UP_RIGHT -> j+1;
        };


        do{
            ChessPiece piece = board.getPiecebyIndex(i_temp, j_temp);
            if(piece == null){
                linearMoves.add(addMove(board, myPosition, i_temp, j_temp));
                // update i_temp or j_temp
                i_temp = (dir == directions.UP || dir == directions.UP_LEFT || dir == directions.UP_RIGHT) ? i_temp-1
                       : (dir == directions.DOWN || dir == directions.DOWN_LEFT || dir == directions.DOWN_RIGHT) ? i_temp+1 
                       : i_temp;
                j_temp = (dir == directions.LEFT || dir == directions.UP_LEFT || dir == directions.DOWN_LEFT) ? j_temp-1
                       : (dir == directions.RIGHT || dir == directions.UP_RIGHT || dir == directions.DOWN_RIGHT) ? j_temp+1 
                       : j_temp;
            }
            else if(piece.getTeamColor() != currentTeamColor){
                linearMoves.add(addMove(board, myPosition, i_temp, j_temp));
                break;
            }
            else{
                break;
            }
        }
        while(cond1.test(i_temp) && cond2.test(j_temp));

        return linearMoves;
    }

 
    private ArrayList<ChessMove> kingMovement(ChessPosition myPosition, ChessBoard board){
        int i = rowToArray(myPosition.getRow());
        int j = colToArray(myPosition.getColumn());
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        // up (left, straight, right)
        if(UL.test(i)){
            kingMoves.add((UL.test(j)) ? addMove(board, myPosition, i-1, j-1) : null);
            kingMoves.add(addMove(board, myPosition, i-1, j));
            kingMoves.add((DR.test(j)) ? addMove(board, myPosition, i-1, j+1) : null);
        }
        // down (left, straight, right)
        if(DR.test(i)){
            kingMoves.add((UL.test(j)) ? addMove(board, myPosition, i+1, j-1) : null);
            kingMoves.add(addMove(board, myPosition, i+1, j));
            kingMoves.add((DR.test(j)) ? addMove(board, myPosition, i+1, j+1) : null);
        }
        // Left, right
        kingMoves.add((UL.test(j)) ? addMove(board, myPosition, i, j-1) : null);
        kingMoves.add((DR.test(j)) ? addMove(board, myPosition, i, j+1) : null);
        return kingMoves;
    }

    private ChessMove addMove(ChessBoard board, ChessPosition myPosition, int i, int j){
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        ChessPiece piece = board.getPiecebyIndex(i, j);
        if(piece == null || piece.getTeamColor() != currentTeamColor){
            ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j));
            return new ChessMove(myPosition, endPosition);
        }
        return null;
    }

    private ChessMove advancePawn(ChessBoard board, ChessPosition myPosition, int i_1, int i_2, int j){
        ChessPiece piece = board.getPiecebyIndex(i_1, j);
        ChessPiece piece2 = board.getPiecebyIndex(i_2,j);
        if(piece == null && piece2 == null){
            ChessPosition endPosition = new ChessPosition(arrayToRow(i_2), arrayToCol(j));
            return new ChessMove(myPosition, endPosition);
        }
        return null;
    }

    private ArrayList<ChessMove> addPawnMove(ChessBoard board, ChessPosition myPosition, int i, int j, boolean take){
        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        ChessPiece piece = board.getPiecebyIndex(i, j);
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        if((take && piece != null && piece.getTeamColor() != currentTeamColor) || (!take && piece == null)){
            ChessPosition endPosition = new ChessPosition(arrayToRow(i), arrayToCol(j));
            if(currentTeamColor == ChessGame.TeamColor.BLACK && i == 7){
                pawnMoves.addAll(promoMoves(myPosition, endPosition));
            }
            else if(currentTeamColor == ChessGame.TeamColor.WHITE && i == 0){
                pawnMoves.addAll(promoMoves(myPosition, endPosition));
            }
            else{
                pawnMoves.add(new ChessMove(myPosition, endPosition));
            }
        }
        return pawnMoves;
    }
    
    private ArrayList<ChessMove> promoMoves(ChessPosition startPosition, ChessPosition endPosition){
        //  Rook, Knight, Bishop, or Queen
        ArrayList<ChessMove> proMoves = new ArrayList<>();
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        proMoves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        return proMoves;
    }

    private int rowToArray(int row){
        return 8-row;
    }

    private int arrayToRow(int i){
        return 8-i;
    }

    private int colToArray(int col){
        return col-1;
    }

    private int arrayToCol(int j){
        return j+1;
    }


}
