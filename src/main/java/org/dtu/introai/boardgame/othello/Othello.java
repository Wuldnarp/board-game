package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;
import org.dtu.introai.boardgame.util.Directions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Othello {

    final Board board;
    HashMap<Cell, Supplier<int[]>> supplyMap;
    Cell current;
    boolean skippedLastAgent;
    boolean complete;
    Cell winner;

    public Othello(Board board, HashMap<Cell, Supplier<int[]>> supplyMap) {
        this.board = board;
        this.complete = false;
        this.winner = Cell.EMPTY;

        this.supplyMap = supplyMap;

        this.current = Cell.BLACK;
    }

    public void gameLoop(Consumer<Object> updateCycle, Consumer<Object> endMessage){

        while (true){
            if(board.getAllLegalMoves(current).isEmpty()){
                if(skippedLastAgent || board.getAllLegalMoves(reverse(current)).isEmpty()){
                    break;
                }
                current = reverse(current);
                skippedLastAgent = true;
                continue;
            }
            skippedLastAgent = false;

            boolean status;
            do {
                int[] move = supplyMap.get(current).get();
                status = setPiece(move[0], move[1], current);
            } while (!status);

            updateCycle.accept(null);

            current = reverse(current);
        }

        setComplete(true);
        endMessage.accept(null);
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

    public void setComplete(boolean complete) {
        setWinner();
        this.complete = complete;
    }

    public void setWinner() {
        this.winner = board.countPieces().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElseThrow();
    }

    public Cell getWinner() {
        return winner;
    }

    public boolean isComplete() {
        return complete;
    }

    public Board getBoard() {
        return board;
    }

    private Cell reverse(Cell cell){
        return cell.equals(Cell.WHITE) ? Cell.BLACK : Cell.WHITE;
    }
}
