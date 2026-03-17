package org.dtu.introai.boardgame.agents;

import java.util.List;
import java.util.Random;
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
        this.color = color;
        this.durationMs = durationMs;
    }

    @Override
    public int[] act(Board board) {

        this.tree = new MCTreeNode(board, null);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < durationMs){ /*MISSING implement is the game over as part of the loop condition */
            MCTreeNode selectedLeaf = select();
            MCTreeNode child = expand(selectedLeaf);
            Cell result = simulate(child);
            backPropagate(result,child);
        }
        return getAction();
    }

    // Helper method for selecting the child with the highest UCB1 value
    // From the book page 209.
    double UCB(MCTreeNode node, MCTreeNode parent) {
        // always explore unvisited nodes first
        if (node.getVisits() == 0) {
            return Double.POSITIVE_INFINITY; 
        }

        // exploration constant, squareroot (2) from book example
        double C = 1.4; 

        // exploitation term: win rate of this node
        // exploration term: prefer nodes visited less relative to parent
        return ((double) node.getWins() / node.getVisits()) + C * Math.sqrt(Math.log(parent.getVisits()) / node.getVisits());
    }

    // selects the child with the highest UCB1 value
    MCTreeNode selectChildWithHighestUCB1(MCTreeNode node) {
        MCTreeNode best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MCTreeNode child : node.getChildren()) {
            double score = UCB(child, node);
            // updates best if this child has a higher score
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
        while (node.getChildren().size() == node.getBoard().getAllLegalMoves(color).size()) {
            node = selectChildWithHighestUCB1(node);
        }
        return node;
    }

    /**
     * @param leaf - the node that needs expansion
     * @return the new node (leaf) that needs simulation
     */

    MCTreeNode expand(MCTreeNode leaf){
        List<int[]> legal = leaf.getBoard().getAllLegalMoves(color);
        int[] action = legal.get(new Random().nextInt(legal.size()));

        // Makes the move using the Othello class already in place
        Othello othello = new Othello(leaf.getBoard());
        othello.setPiece(action[0], action[1], color);

        // returns the resulting board state
        // applies the action to get the resulting board state
        Board childState = othello.getBoard();

        // creates a new child node with the resulting state and parent reference
        MCTreeNode child = new MCTreeNode(childState, leaf);

        // adds the child to the tree
        leaf.addChild(child);
        return child;
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
        } while(simulatedGame.isComplete()); //todo finish while loop when game is over

        return null;
    }

    /**
     * @param result - the winder of the simulation
     * @param child - the given node the simulation was run on
     */
    void backPropagate(Cell result, MCTreeNode child){
        MCTreeNode curNode = child;
        
        while(curNode != null){
            curNode.incrementVisit();
            
            if(result == color){
                curNode.incrementWins();
            } else if (result != Cell.EMPTY) {
                curNode.incrementLosses();
            }

            curNode = curNode.getParent();
        }
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
