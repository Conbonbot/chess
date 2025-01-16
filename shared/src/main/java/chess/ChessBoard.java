package chess;

import java.util.ArrayList;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ArrayList<ChessPieceWithPosition> gamePieces = new ArrayList<>();

    /*
     * Constructor for the class
     */
    public ChessBoard() {
    }

    /*
     * An array holding the relative locations of the special pieces
     */
    private final ChessPiece.PieceType[] specialPiecesOrder = {
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
    private void setWhite(){
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
    private void setBlack(){
        int col = 1;
        // Set special pieces (row = 7)
        for(ChessPiece.PieceType specialPiece : specialPiecesOrder){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.BLACK, specialPiece), 
                new ChessPosition(8, col++)
            ));
        }
        // Set pawns (row = 8)
        col = 1;
        for(int i = 0; i < 8; i++){
            gamePieces.add(new ChessPieceWithPosition(
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), 
                new ChessPosition(7, col++)
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
        for(ChessPieceWithPosition piece : gamePieces){
            if(piece.getPosition() == position)
                return piece.getPiece();
        }
        return null;
    }

    public ArrayList<ChessPieceWithPosition> getBoard(){
        return this.gamePieces;
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

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        ChessBoard check = (ChessBoard) obj;
        ArrayList<ChessPieceWithPosition> checkBoard = check.getBoard();
        // Check one way
        boolean equal = false;
        for(ChessPieceWithPosition gamePiece : this.gamePieces){
            equal = false;
            for(ChessPieceWithPosition checkPiece : checkBoard){
                if(gamePiece.equals(checkPiece)){
                    equal = true;
                    break;
                }
            }
            if(!equal){
                break;
            }
        }
        return equal;
    
    }
    
    @Override
    public int hashCode(){
        return gamePieces.hashCode();
    }

    // TODO: Remove
    private static final Map<ChessPiece.PieceType, Character> CHAR_TO_TYPE_MAP = Map.of(
        ChessPiece.PieceType.PAWN, 'p',
        ChessPiece.PieceType.KNIGHT, 'n',
        ChessPiece.PieceType.ROOK,'r', 
        ChessPiece.PieceType.QUEEN,'q',
        ChessPiece.PieceType.KING, 'k', 
        ChessPiece.PieceType.BISHOP, 'b');

    @Override
    public String toString(){
        String str = "";
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                boolean found = false;
                for(ChessPieceWithPosition piece : this.gamePieces){
                    ChessPosition pos = piece.getPosition();
                    if(pos.getRow() == i && pos.getColumn() == j){
                        str += "|";
                        if(piece.getPiece().getTeamColor() == ChessGame.TeamColor.WHITE)
                            str += CHAR_TO_TYPE_MAP.get(piece.getPiece().getPieceType());
                        else
                            str += CHAR_TO_TYPE_MAP.get(piece.getPiece().getPieceType()).toString().toUpperCase();
                        found = true;
                        break;
                    }
                }
                if(!found)
                    str += "| ";
            }
            str += "|\n";
        }
        return str;
    }
}
