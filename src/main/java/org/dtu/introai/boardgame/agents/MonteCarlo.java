package org.dtu.introai.boardgame.agents;

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

    MCTreeNode select(){
        // TODO implement select logic
        return null;
    }
    MCTreeNode expand(MCTreeNode leaf){
        // TODO implement expand logic
        return null;
    }
    Cell simulate(MCTreeNode child){
        // TODO implement simulation logic
    }
    void backPropagate(Cell result, MCTreeNode child){
        // TODO implement back propegation
    }
}
