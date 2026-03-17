package org.dtu.introai.boardgame.agents;

import javax.swing.Action;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

public class MonteCarlo implements Agent {

    private MCTreeNode tree;

    public MonteCarlo(){
        this.tree = new MCTreeNode();
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

    // Helper method for selecting the child with the highest UCB1 value
    // From the book page 209.
    double UCB(MCTreeNode node, MCTreeNode parent) {
        if (node.getN() == 0) {
            return Double.POSITIVE_INFINITY; 
        }
        double C = 1.4;
        return (node.getU() / node.getN()) + C * Math.sqrt(Math.log(parent.getN()) / node.getN());
    }

    MCTreeNode select(){
        // TODO implement select logic
        MCTreeNode node = tree;
        while (isFullyExpanded(node) && !isTerminal(node)) {
            node = selectChildWithMaxUCB1(node);
        }
        return node;
    }

    MCTreeNode expand(MCTreeNode leaf){
        // TODO implement expand logic
        if (isTerminal(leaf.getState())) {
            return leaf;
        }
        Action action = getRandomUntriedAction(leaf);
        Board childState = getResultingState(leaf.getState(), action);
        MCTreeNode child = new MCTreeNode(childState, leaf);
        leaf.addChild(child);
        return child;
    }
    Cell simulate(MCTreeNode child){
        // TODO implement simulation logic
    }
    void backPropagate(Cell result, MCTreeNode child){
        // TODO implement back propegation
    }
}
