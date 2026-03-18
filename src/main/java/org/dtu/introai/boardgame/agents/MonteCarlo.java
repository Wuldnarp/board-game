package org.dtu.introai.boardgame.agents;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.dtu.introai.boardgame.agents.types.MCTreeNode;
import org.dtu.introai.boardgame.api.Agent;
import org.dtu.introai.boardgame.othello.Othello;
import org.dtu.introai.boardgame.util.Board;
import org.dtu.introai.boardgame.util.Cell;

import javax.swing.*;

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
    double upperConfidentBound(MCTreeNode node, MCTreeNode parent) {
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

    // selects the child with the highest upper confident bound value
    MCTreeNode selectChildWithHighestUpperConfidentBound(MCTreeNode node) {
        MCTreeNode best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (MCTreeNode child : node.getChildren()) {
            double score = upperConfidentBound(child, node);
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
        int depth = 0;

        while (!node.getChildren().isEmpty()) {
            Cell currentColor = (depth % 2 == 0) ? color : reverse(color);
            int legalMoves = node.getBoard().getAllLegalMoves(currentColor).size();

            if (node.getChildren().size() < legalMoves) {
                return node; // not fully expanded yet
            }

            node = selectChildWithHighestUpperConfidentBound(node);
            depth++;
        }
        return node;
    }

    /**
     * @param leaf - the node that needs expansion
     * @return the new node (leaf) that needs simulation
     */
    MCTreeNode expand(MCTreeNode leaf){
        // derive whose turn it is from depth
        int depth = 0;
        MCTreeNode temp = leaf;
        while(temp.getParent() != null){
            depth++;
            temp = temp.getParent();
        }
        Cell currentColor = (depth % 2 == 0) ? color : reverse(color);

        List<int[]> legal = leaf.getBoard().getAllLegalMoves(currentColor);
        if (legal.isEmpty()) return leaf;
        int[] action = legal.get(new Random().nextInt(legal.size()));

        Board copy = new Board(leaf.getBoard());
        Othello othello = new Othello(copy, null);
        othello.setPiece(action[0], action[1], currentColor); // ← also fixed here

        MCTreeNode child = new MCTreeNode(copy, leaf, action);
        leaf.addChild(child);
        return child;
    }

    /**
     * @param child - the expanded new node to run a simulation on
     * @return the winner of the simulation
     */
    Cell simulate(MCTreeNode child){

        HashMap<Cell, Supplier<int[]>> supplyMap = new HashMap<>();

        Othello simulatedGame = new Othello(child.getBoard(), supplyMap);

        Supplier<int[]> supplyLoopWhite = new Supplier<int[]>() {
            @Override
            public int[] get() {
                List<int[]> moves = simulatedGame.getBoard().getAllLegalMoves(Cell.WHITE);
                return moves.get(new Random().nextInt(moves.size()));
            }
        };

        Supplier<int[]> supplyLoopBlack = new Supplier<int[]>() {
            @Override
            public int[] get() {
                List<int[]> moves = simulatedGame.getBoard().getAllLegalMoves(Cell.BLACK);
                return moves.get(new Random().nextInt(moves.size()));
            }
        };

        Consumer<Object> consumer = new Consumer<Object>() {
            @Override
            public void accept(Object o) {

            }
        };
        supplyMap.put(Cell.WHITE, supplyLoopWhite);
        supplyMap.put(Cell.BLACK, supplyLoopBlack);
        simulatedGame.setSupplyMap(supplyMap);
        simulatedGame.gameLoop(consumer, consumer);
        return simulatedGame.getWinner();

//        Random r = new Random();
//        Othello simulatedGame = new Othello(child.getBoard(),null);
//        Cell playerCell = color; //always start with the play of the current color
//        int[] move;
//        List<int[]> moves;
//        Board board = child.getBoard();
//        do{ //play game untill its finished
//            moves = board.getAllLegalMoves(playerCell); //get all posible moves
//            playerCell = color == Cell.WHITE ? Cell.BLACK : Cell.WHITE; //reverse player cell
//            if(moves.isEmpty()){ //if empty go to next loop, where if game is ended will be evaluated as well
//                continue;
//            }
//            move = moves.get(r.nextInt(moves.size())); //pick a random move
//            simulatedGame.setPiece(move[0], move[1], color); //place a peace on board
//            board = simulatedGame.getBoard(); //get new board
//            MCTreeNode newChild = new MCTreeNode(new Board(board), child);
//            child.addChild(newChild);
//            child = newChild;
//        } while(simulatedGame.isComplete()); //todo finish while loop when game is over
//        return simulatedGame.getWinner();
    }

    /**
     * @param result - the winner of the simulation
     * @param child - the given node the simulation was run on
     */
    void backPropagate(Cell result, MCTreeNode child){
        // derive starting color from depth (root = color, children alternate)
        int depth = 0;
        MCTreeNode temp = child;
        while(temp.getParent() != null){
            depth++;
            temp = temp.getParent();
        }
        Cell nodeColor = (depth % 2 == 0) ? color : reverse(color);

        MCTreeNode curNode = child;
        while(curNode != null){
            curNode.incrementVisit();

            if(result == nodeColor){
                curNode.incrementWins();
            } else {
                curNode.incrementLosses();
            }

            nodeColor = reverse(nodeColor);
            curNode = curNode.getParent();
        }
    }

    private Cell reverse(Cell cell){
        return cell.equals(Cell.WHITE) ? Cell.BLACK : Cell.WHITE;
    }

    /**
     * pick the action with the highest number of playouts
     * @return the chosen action
     */
    int[] getAction(){
        return tree.getChildren().stream()
            .max(Comparator.comparingInt(MCTreeNode::getVisits))
            .orElseThrow()
            .getMove();
    }
}
