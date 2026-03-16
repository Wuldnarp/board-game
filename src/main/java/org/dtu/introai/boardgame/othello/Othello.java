package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;
import org.dtu.introai.boardgame.util.Directions;

import java.util.ArrayList;

public class Othello {

    final Board board;

    public Othello(Board board) {
        this.board = board;
    }

    /**
     * Set a piece on the board. <br/>
     *
     * @param row   the desired row
     * @param col   the desired col
     * @param piece the color of the piece
     * @return true if the move is accepted otherwise false
     */
    public boolean setPiece(int row, int col, Cell piece) {
        if (!board.isLegal(row, col, piece)) {
            return false;
        }

        board.getPlayingBoard()[row][col] = piece;
        Cell reverseColor = piece.equals(Cell.WHITE) ? Cell.BLACK : Cell.WHITE;

        for (int[] direction : Directions.ALL) {
            int dirRow = direction[0] + row;
            int dirCol = direction[1] + col;

            ArrayList<int[]> flips = new ArrayList<>();

            // collect all pieces in one direction
            while (board.inBounds(dirRow, dirCol) && board.getPlayingBoard()[dirRow][dirCol].equals(reverseColor)) {
                flips.add(new int[]{dirRow, dirCol});
                dirRow += direction[0];
                dirCol += direction[1];
            }

            // flip pieces if terminated by the same color as placed
            if (!flips.isEmpty() && board.inBounds(dirRow, dirCol) && board.getPlayingBoard()[dirRow][dirCol].equals(piece)) {
                for (int[] flip : flips) {
                    board.getPlayingBoard()[flip[0]][flip[1]] = piece;
                }
            }
        }
        return true;
    }

    public Board getBoard() {
        return board;
    }
}
