package org.dtu.introai.boardgame.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Board {

    private final Cell[][] playingBoard;
    private final int boardSize;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.playingBoard = new Cell[boardSize][boardSize];

        //fill each row in the board
        for(Cell[] row : playingBoard){
            Arrays.fill(row,Cell.EMPTY);
        }

        int middle = boardSize / 2;

        // add the white starting pieces
        playingBoard[middle - 1][middle - 1] = Cell.WHITE;
        playingBoard[middle][middle] = Cell.WHITE;

        // add the black starting pieces
        playingBoard[middle - 1][middle] = Cell.BLACK;
        playingBoard[middle][middle - 1] = Cell.BLACK;
    }

    public Cell[][] getPlayingBoard() {
        return playingBoard;
    }

    public boolean inBounds(int row, int col){
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    /**
     * returns the legality of a move. <br/>
     * A move is legal when: <br/>
     *      1. The target cell is empty. <br/>
     *      2. In at least one direction there is a contiguous run of opponent pieces that is terminated by one of
     *      `player`'s own pieces — meaning at least one opponent piece would be flipped.
     * @param row the desired row
     * @param col the desired col
     * @param actor the piece in question
     * @return true if the move is legal to make otherwise false
     */
    public boolean isLegal(int row, int col, Cell actor){

        // return false if the spot is not unoccupied
        if(!playingBoard[row][col].equals(Cell.EMPTY)){
            return false;
        }

        Cell reverseColor = actor.equals(Cell.WHITE) ? Cell.WHITE : Cell.BLACK;

        for(int[] direction : Directions.ALL){
            int dirRow = row + direction[0];
            int dirCol = col + direction[1];

            boolean hasReverse = false;

            // Walk until not landing on opponent pieces
            while(inBounds(dirRow,dirCol) && playingBoard[dirRow][dirCol].equals(reverseColor)){
                hasReverse = true;

                dirRow += direction[0];
                dirCol += direction[1];
            }

            // Legal if the run of opponent pieces is capped by our own piece
            if(hasReverse && inBounds(dirRow,dirCol) && playingBoard[dirRow][dirCol].equals(actor)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return the position of all legal moves
     * @param actor the color in question
     * @return a list of legal moves
     */
    public List<int[]> getAllLegalMoves(Cell actor){
        ArrayList<int[]> legalMoves = new ArrayList<>();

        for(int i = 0; i < playingBoard.length; i++ ){
            for(int j = 0; j < playingBoard[i].length; j++){
                if(isLegal(i,j,actor)){
                    legalMoves.add(new int[]{i,j});
                }
            }
        }
        return legalMoves;
    }

    /**
     * Get the count of black and white pieces on the board
     * @return the count of each color in a map
     */
    public Map<Cell,Integer> countPieces(){
        int blackCount = 0;
        int whiteCount = 0;

        for(Cell[] row : playingBoard){
            for(Cell piece : row){
                if(piece.equals(Cell.BLACK)){
                    blackCount++;
                } else if (piece.equals(Cell.WHITE)) {
                    whiteCount++;
                }
            }
        }
        return Map.of(Cell.BLACK,blackCount,Cell.WHITE,whiteCount);
    }
}
