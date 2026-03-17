package org.dtu.introai.boardgame.othello;

import org.dtu.introai.boardgame.api.Agent;
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
    HashMap<Cell, Agent> agentMap;
    HashMap<Cell, Supplier<int[]>> supplyMap;
    Cell currant;
    boolean skippedLastAgent;
    boolean complete;
    Cell winer;

    public Othello(Board board, HashMap<Cell, Agent> agentMap, HashMap<Cell, Supplier<int[]>> supplyMap) {
        this.board = board;
        this.complete = false;
        this.winer = Cell.EMPTY;

        this.agentMap = agentMap;
        this.supplyMap = supplyMap;

        this.currant = Cell.BLACK;
    }

    public void gameLoop(Consumer<Object> updateCycle, Consumer<Object> endMessage){

        while (true){
            if(board.getAllLegalMoves(currant).isEmpty()){
                if(skippedLastAgent || board.getAllLegalMoves(reverse(currant)).isEmpty()){
                    break;
                }
                currant = reverse(currant);
                skippedLastAgent = true;
                continue;
            }
            skippedLastAgent = false;

            boolean status;
            do {
                int[] move = supplyMap.get(currant).get();
                status = setPiece(move[0], move[1], currant);
            } while (!status);

            updateCycle.accept(null);

            currant = reverse(currant);
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
        setWinder();
        this.complete = complete;
    }

    public void setWinder() {
        this.winer = board.countPieces().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElseThrow();
    }

    public Cell getWiner() {
        return winer;
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
