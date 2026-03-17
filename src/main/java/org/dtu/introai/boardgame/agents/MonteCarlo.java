package org.dtu.introai.boardgame.agents;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import java.util.List;
import java.util.Random;

public class MonteCarlo implements Agent {

    private MCTreeNode tree;
    private Cell color;
    private long durationMs;

    public MonteCarlo(Cell color, long durationMs){
        this.tree = new MCTreeNode();
        this.color = color;
        this.durationMs = durationMs;
    }

    @Override
    public int[] act(Board board) {

        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < durationMs){
            MCTreeNode selectedLeaf = select();
            MCTreeNode child = expand(selectedLeaf);
            Cell result = simulate(child);
            backPropagate(result,child);
        }
        return getAction();
    }

    /**
     * @return the leaf node for expansion
     */
    MCTreeNode select(){
        // TODO implement select logic
        return null;
    }

    /**
     * @param leaf - the node that needs expansion
     * @return the new node (leaf) that needs simulation
     */
    MCTreeNode expand(MCTreeNode leaf){
        // TODO implement expand logic
        return null;
    }

    /**
     * @param child - the expanded new node to run a simulation on
     * @return the winder of the simulation
     */
    Cell simulate(MCTreeNode child){
        Random r = new Random();
        Othello simulatedGame = new Othello(child.getBoard());
        Cell playerCell = color; //always start with the play of the current color
        int[] move;
        List<int[]> moves;
        Board board = child.getBoard();
        do{ //play game untill its finished
            moves = board.getAllLegalMoves(playerCell); //get all posible moves
            playerCell = color == Cell.WHITE ? Cell.BLACK : Cell.WHITE; //reverse player cell
            if(moves.isEmpty()){ //if empty go to next loop, where if game is ended will be evaluated as well
                continue;
            }
            move = moves.get(r.nextInt(moves.size())); //pick a random move
            simulatedGame.setPiece(move[0], move[1], color); //place a peace on board
            board = simulatedGame.getBoard(); //get new board
        } while(true); //todo finish while loop when game is over

        return null;
    }

    /**
     * @param result - the winder of the simulation
     * @param child - the given node the simulation was run on
     */
    void backPropagate(Cell result, MCTreeNode child){
        // TODO implement back propegation
    }

    /**
     * pick the action with the highest number of playouts
     * @return the chosen action
     */
    int[] getAction(){
        // TODO implement
        return new int[2];
    }
}
