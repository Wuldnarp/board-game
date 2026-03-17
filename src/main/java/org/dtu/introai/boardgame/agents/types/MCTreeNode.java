package org.dtu.introai.boardgame.agents.types;

import org.dtu.introai.boardgame.util.Board;

import java.util.ArrayList;

public class MCTreeNode {

    private int wins;
    private int losses;
    private int visits;
    private ArrayList<MCTreeNode> children;
    private MCTreeNode parent;
    private Board board;
    private int[] move;

    public MCTreeNode(Board board, MCTreeNode parent, int[] move){
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.children = new ArrayList<>();
    }

    public MCTreeNode(Board board, MCTreeNode parent){
        this(board, parent, null);
    }

    public int[] getMove() {
        return move;
    }

    public ArrayList<MCTreeNode> getChildren() {
        return children;
    }

    public MCTreeNode getParent() {
        return parent;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public int getVisits() {
        return visits;
    }

    public void incrementVisit(){
        visits++;
    }

    public void incrementWins(){
        wins++;
    }
    public void incrementLosses(){
        losses++;
    }

    public void addChild(MCTreeNode child){
        children.add(child);
    }

    public Board getBoard() {
        return board;
    }
}
