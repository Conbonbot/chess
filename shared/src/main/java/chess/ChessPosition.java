package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    // Global variables for current row and col position of piece
    int row, col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /*
     * Sets the row of a given piece
     * @param row The row to set (1-8)
     */
    public void setRow(int row){
        this.row = row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /*
     * Sets the column of a piece
     * @param col The column to set (1-8)
     */
    public void setCol(int col){
        this.col = col;
    }
}
