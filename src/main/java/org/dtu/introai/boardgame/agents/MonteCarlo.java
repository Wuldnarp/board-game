package org.dtu.introai.boardgame.agents;

import javax.swing.Action;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

public class MonteCarlo implements Agent {

    private MCTreeNode tree;
    private Cell color;

    public MonteCarlo(Cell color){
        this.tree = new MCTreeNode();
        this.color = color;
    }

    @Override
    public int[] act(Board board) {

        while(true){
            MCTreeNode selectedLeaf = select();
            MCTreeNode child = expand(selectedLeaf);
            Cell result = simulate(child);
            backPropagate(result,child);
        }
        return new int[0];
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

    // Helper method for selecting the child with the highest UCB1 value
    // From the book page 209.
    double UCB(MCTreeNode node, MCTreeNode parent) {
        if (node.getVisits() == 0) {
            return Double.POSITIVE_INFINITY; 
        }
        double C = 1.4;
        return ((double) node.getWins() / node.getVisits()) + C * Math.sqrt(Math.log(parent.getVisits()) / node.getVisits());
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
    MCTreeNode expand(MCTreeNode leaf){
        Action action = getRandomUntriedAction(leaf);
        Board childState = getResultingState(leaf.getBoard(), action);
        MCTreeNode child = new MCTreeNode(childState, leaf);
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
}
