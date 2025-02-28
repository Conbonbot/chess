package chess;

import java.util.ArrayList;
import java.util.Collection;



/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTeamColor;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        currentTeamColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;   
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    
    public void changeTeamTurn(){
        currentTeamColor = (currentTeamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor teamColor = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        for(ChessMove possibleMove : possibleMoves){
            if(legal(possibleMove, teamColor)){
                legalMoves.add(possibleMove);
            }
        }
        return legalMoves;
    }
    /**
     * 
     * @param move chess move to check validity
     * @return boolean if move is legal (doesn't cause danger of check)
     */
    private boolean legal(ChessMove move, TeamColor teamColor){
        // Set copied board to the end result of the move
        // Check if any piece on the other team has the king as a move
        ChessPosition kingPosition = board.getKingLocation(teamColor);
        ChessPiece[][] cBoard = copyBoard();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        // Make move
        fakeMove(cBoard, startPosition, endPosition);
        // Move king position as well
        if(move.getStartPosition().equals(kingPosition)){
            kingPosition = move.getEndPosition();
        }
        ChessBoard copyBoard = new ChessBoard();
        copyBoard.setBoard(cBoard);
        // Find other pieces
        return teamTakeKing(copyBoard, cBoard, teamColor, kingPosition);
    }

    /**
     * 
     * @param copyBoard
     * @param cBoard
     * @param teamColor
     * @param kingPosition
     * @return If the other team can take the king
     */
    private boolean teamTakeKing(ChessBoard copyBoard, ChessPiece[][] cBoard, TeamColor teamColor, ChessPosition kingPosition){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                // Check for other pieces
                if(cBoard[i][j] != null && cBoard[i][j].getTeamColor() != teamColor){
                    // Check if other piece could take king
                    if(!containsKing(copyBoard, cBoard, kingPosition, i, j)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean containsKing(ChessBoard copyBoard, ChessPiece[][] cBoard, ChessPosition kingPosition, int i, int j){
        // Check if other piece could take king
        ChessPiece otherPiece = cBoard[i][j];
        for(ChessMove otherMove : otherPiece.pieceMoves(copyBoard, new ChessPosition(arrayToRow(i), arrayToCol(j)))){
            if(otherMove.getEndPosition().equals(kingPosition)){
                return false;
            }
        }
        return true;
    }


    private ChessPiece[][] copyBoard(){
        if(board == null){
            return null;
        }
        ChessPiece[][] cBoard = new ChessPiece[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPiece piece = board.getPiecebyIndex(i, j);
                if(piece != null){
                    cBoard[i][j] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                }
            }
        }
        
        return cBoard;
    }

    private void fakeMove(ChessPiece[][] cBoard, ChessPosition startPosition, ChessPosition endPosition){
        cBoard[rowToArray(endPosition.getRow())][colToArray(endPosition.getColumn())] = 
            cBoard[rowToArray(startPosition.getRow())][colToArray(startPosition.getColumn())];
        cBoard[rowToArray(startPosition.getRow())][colToArray(startPosition.getColumn())] = null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition pos = move.getStartPosition();
        ChessPiece piece = board.getPiece(pos);
        if(piece == null){
            throw new InvalidMoveException("Piece doesn't exist");
        }
        if(piece.getTeamColor() != currentTeamColor){
            throw new InvalidMoveException("It is not your turn");
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if(legalMoves.isEmpty()){
            throw new InvalidMoveException("No valid moves.");
        }
        for(ChessMove legalMove : legalMoves){
            if(legalMove.equals(move)){
                board.movePiece(move);
                changeTeamTurn();
                return;
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Go through the team color
        // If any valid move can take the king, check
        ArrayList<ChessPosition> oppoTeamPositions = board.getTeamPositions(teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        ChessPosition kingPosition = board.getKingLocation(teamColor);
        for(ChessPosition oppoPiece : oppoTeamPositions){
            for(ChessMove validOppoPieceMove : validMoves(oppoPiece)){
                if(validOppoPieceMove.getEndPosition().equals(kingPosition)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Check if any team piece can capture a piece to remove check
        if(isInCheck(teamColor)){
            ArrayList<ChessPosition> teamPositions = board.getTeamPositions(teamColor);
            for(ChessPosition pos : teamPositions){
                // Check if a valid move can remove the piece causing check
                // Test if any valid move can remove check
                for(ChessMove move : validMoves(pos)){
                    // fake move
                    // Check if oppo team's move hold king
                    ChessPosition kingPosition = board.getKingLocation(teamColor);
                    ChessPiece[][] cBoard = copyBoard();
                    ChessPosition startPosition = move.getStartPosition();
                    ChessPosition endPosition = move.getEndPosition();
                    // Fake move
                    fakeMove(cBoard, startPosition, endPosition);
                    if(move.getStartPosition().equals(kingPosition)){
                        kingPosition = move.getEndPosition();
                    }
                    ChessBoard copyBoard = new ChessBoard();
                    copyBoard.setBoard(cBoard);
                    // Check if other team holds king
                    TeamColor other = (teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
                    if(teamTakeKing(copyBoard, cBoard, other, kingPosition)){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        ArrayList<ChessPosition> teamPositions = board.getTeamPositions(teamColor);
        for(ChessPosition piece : teamPositions){
            if(!validMoves(piece).isEmpty()){
                return false;
            }   
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString(){
        String str = "";
        str += board.toString();
        str += "Current Color: ";
        str += currentTeamColor + "\n";
        return str;
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
