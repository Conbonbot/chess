package chess;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame.TeamColor;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor currentTeamColor;

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
    // TODO: Implement
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        for(ChessMove possibleMove : possibleMoves){
            if(legal(possibleMove)){
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
    // TODO: Implement
    public boolean legal(ChessMove move){
        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        for(ChessMove legalMove : legalMoves){
            if(legalMove == move){
                board.movePiece(move);
                return;
            }
        }
        throw new InvalidMoveException("Move is invalid.");
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
                if(validOppoPieceMove.getEndPosition() == kingPosition){
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
        ChessPosition kingPosition = board.getKingLocation(teamColor);
        return isInCheck(teamColor) && validMoves(kingPosition).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
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
}
