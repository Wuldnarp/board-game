package org.dtu.introai.boardgame.agents;

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
