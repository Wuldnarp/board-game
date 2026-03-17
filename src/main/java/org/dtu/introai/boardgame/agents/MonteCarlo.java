package org.dtu.introai.boardgame.agents;

import java.util.List;
import java.util.Random;

// import javax.swing.Action;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

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

        this.tree = new MCTreeNode(board, null);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < durationMs){
            MCTreeNode selectedLeaf = select();
            MCTreeNode child = expand(selectedLeaf);
            Cell result = simulate(child);
            backPropagate(result,child);
        }
    }

    // Helper method for selecting the child with the highest UCB1 value
    // From the book page 209.
    double UCB(MCTreeNode node, MCTreeNode parent) {
        if (node.getVisits() == 0) {
            return Double.POSITIVE_INFINITY; 
        }
        double C = 1.4;
        return ((double) node.getWins() / node.getVisits()) + C * Math.sqrt(Math.log(parent.getVisits()) / node.getVisits());
    }

    //selection node with the highest UCB1 value
    MCTreeNode selectChildWithMaxUCB1(MCTreeNode node) {
    MCTreeNode best = null;
    double bestScore = Double.NEGATIVE_INFINITY;

        for (MCTreeNode child : node.getChildren()) {
            double score = UCB(child, node);
            if (score > bestScore) {
                bestScore = score;
                best = child;
            }
        }
        return best;
    }

    /**
     * @return the leaf node for expansion
     */
    MCTreeNode select(){
        MCTreeNode node = tree;
        while (isFullyExpanded(node)) {
            node = selectChildWithMaxUCB1(node);
        }
        return node;
    }

    // Helper method to check if a node is fully expanded for expansion
    boolean isFullyExpanded(MCTreeNode node) {
        return node.getChildren().size() == node.getBoard().getAllLegalMoves(color).size();
    }

    /**
     * @param leaf - the node that needs expansion
     * @return the new node (leaf) that needs simulation
     */

    // We need a copy of the board to apply the move and get the resulting state for the new child node
    // Without it we would be modifying the board state of the parent node which would affect the rest of the tree
    Board getResultingState(Board current, int[] action) {
        // copy the current board
        Board copy = new Board(current.boardSize);
        for (int i = 0; i < current.boardSize; i++)
            for (int j = 0; j < current.boardSize; j++)
                copy.getPlayingBoard()[i][j] = current.getPlayingBoard()[i][j];

        // apply the move on the copy using existing Othello logic
        Othello othello = new Othello(copy);
        othello.setPiece(action[0], action[1], color);
        return copy;
    }

    MCTreeNode expand(MCTreeNode leaf){
        // get all legal moves from the current board state
        List<int[]> legal = leaf.getBoard().getAllLegalMoves(color);
        // pick a random action
        int[] action = legal.get(new Random().nextInt(legal.size()));
        // apply the action to get the resulting board state
        Board childState = getResultingState(leaf.getBoard(), action);
        // create a new child node with the resulting state and parent reference
        MCTreeNode child = new MCTreeNode(childState, leaf);
        // add the child to the tree
        leaf.addChild(child);
        return child;
    }  

    /**
     * @param child - the expanded new node to run a simulation on
     * @return the winder of the simulation
     */
    Cell simulate(MCTreeNode child){
        // TODO implement simulation logic
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
